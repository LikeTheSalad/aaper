package com.likethesalad.android.aaper.plugin.appender

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction


abstract class CodeAppenderTask : DefaultTask() {

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val jarOutput = JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(outputFile.get().asFile)
            )
        )
        addJarsToTheOutput(jarOutput)

        allDirectories.get().forEach { directory ->
            val dirFile = directory.asFile
            val dirURI = dirFile.toURI()
            dirFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = getRelativePath(file, dirURI)
                    addFileToTheOutput(file, curatePath(relativePath), jarOutput)
                }
            }
        }

        jarOutput.close()
    }

    private fun getRelativePath(file: File, dirURI: URI): String {
        return dirURI.relativize(file.toURI()).path
    }

    private fun curatePath(path: String): String {
        return path.replace(File.separatorChar, '/')
    }

    private fun addFileToTheOutput(
        file: File,
        relativePath: String,
        jarOutput: JarOutputStream
    ) {
        jarOutput.putNextEntry(JarEntry(relativePath))
        file.inputStream().use { inputStream ->
            inputStream.copyTo(jarOutput)
        }
        jarOutput.closeEntry()
    }

    private fun addJarsToTheOutput(jarOutput: JarOutputStream) {
        allJars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                jarOutput.putNextEntry(JarEntry(jarEntry.name))
                jarFile.getInputStream(jarEntry).use {
                    it.copyTo(jarOutput)
                }
                jarOutput.closeEntry()
            }
            jarFile.close()
        }
    }
}