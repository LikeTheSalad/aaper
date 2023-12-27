package com.likethesalad.android.aaper.plugin.appender

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.plugin.appender.data.MethodInfo
import com.likethesalad.android.aaper.plugin.appender.visitor.WraaperClassCreator
import com.likethesalad.android.aaper.plugin.appender.visitor.data.ClassName
import com.likethesalad.android.aaper.plugin.utils.NamingUtils.getGeneratedClassSimpleName
import io.github.classgraph.ClassGraph
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.Type

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
        findAnnotatedMethods().forEach {
            generateAndAddClassToTheOutput(jarOutput, it)
        }

        addProjectClassesToTheOutput(jarOutput)

        jarOutput.close()
    }

    private fun findAnnotatedMethods(): List<MethodInfo> {
        val annotatedMethods = mutableListOf<MethodInfo>()
        ClassGraph()
            .overrideClasspath(allDirectories.get().map { it.asFile })
            .ignoreMethodVisibility()
            .enableAnnotationInfo()
            .scan().use { scanResult ->
                scanResult.getClassesWithMethodAnnotation(EnsurePermissions::class.java)
                    .forEach { classInfo ->
                        classInfo.declaredMethodInfo.forEach { methodInfo ->
                            if (methodInfo.hasAnnotation(EnsurePermissions::class.java)) {
                                annotatedMethods.add(
                                    MethodInfo(
                                        methodInfo.name,
                                        methodInfo.typeDescriptorStr,
                                        ClassName(classInfo.packageName, classInfo.simpleName)
                                    )
                                )
                            }
                        }
                    }
            }

        return annotatedMethods
    }

    private fun generateAndAddClassToTheOutput(jarOutput: JarOutputStream, methodInfo: MethodInfo) {
        val containerClassName = methodInfo.className
        val generatedClassName = ClassName(
            containerClassName.packageName,
            getGeneratedClassSimpleName(containerClassName.simpleName, methodInfo.name)
        )
        val generatedClassBytes = generateClass(methodInfo, generatedClassName, containerClassName)

        addClassToTheOutput(jarOutput, generatedClassName, generatedClassBytes)
    }

    private fun generateClass(
        methodInfo: MethodInfo,
        generateClassName: ClassName,
        containerClassName: ClassName
    ): ByteArray {
        val methodType = Type.getMethodType(methodInfo.descriptor)
        return WraaperClassCreator.create(
            generateClassName,
            methodInfo.name,
            Type.getType("L${containerClassName.internalName};"),
            *methodType.argumentTypes
        )
    }

    private fun addClassToTheOutput(
        jarOutput: JarOutputStream,
        generateClassName: ClassName,
        generatedClassBytes: ByteArray
    ) {
        jarOutput.putNextEntry(ZipEntry(generateClassName.fileName))
        jarOutput.write(generatedClassBytes)
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

    private fun addProjectClassesToTheOutput(jarOutput: JarOutputStream) {
        allDirectories.get().forEach { directory ->
            val dirFile = directory.asFile
            val dirUri = dirFile.toURI()
            dirFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = getRelativePath(file, dirUri)
                    addFileToTheOutput(file, curatePath(relativePath), jarOutput)
                }
            }
        }
    }

    private fun getRelativePath(file: File, dirUri: URI): String {
        return dirUri.relativize(file.toURI()).path
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
}