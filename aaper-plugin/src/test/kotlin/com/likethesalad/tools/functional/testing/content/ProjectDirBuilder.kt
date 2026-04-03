package com.likethesalad.tools.functional.testing.content

import java.io.File

class ProjectDirBuilder(private val inputDir: File) {

    fun buildDirectory(projectDir: File) {
        val destinationDir = File(projectDir, "src").apply { mkdirs() }
        copyRecursively(inputDir, destinationDir)
    }

    private fun copyRecursively(source: File, destination: File) {
        source.listFiles()?.forEach { file ->
            val target = File(destination, file.name)
            if (file.isDirectory) {
                target.mkdirs()
                copyRecursively(file, target)
            } else {
                target.parentFile.mkdirs()
                file.copyTo(target, overwrite = true)
            }
        }
    }
}
