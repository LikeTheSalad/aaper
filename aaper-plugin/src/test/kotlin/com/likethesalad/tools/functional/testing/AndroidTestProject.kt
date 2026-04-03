package com.likethesalad.tools.functional.testing

import com.likethesalad.tools.functional.testing.descriptor.ProjectDescriptor
import java.io.File
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class AndroidTestProject(val rootDir: File) {
    private val rootGradleFile = File(rootDir, BUILD_GRADLE_FILE_NAME)
    private val settingsFile = File(rootDir, SETTINGS_GRADLE_FILE_NAME)

    init {
        setUpRootProject()
    }

    fun runGradle(
        forProjectName: String,
        command: String,
        withInfo: Boolean = false
    ): BuildResult = runGradle(forProjectName, listOf(command), withInfo)

    fun runGradleAndFail(
        forProjectName: String,
        command: String,
        withInfo: Boolean = false
    ): BuildResult = runGradleAndFail(forProjectName, listOf(command), withInfo)

    fun runGradle(
        forProjectName: String,
        commands: List<String>,
        withInfo: Boolean = false
    ): BuildResult {
        return createGradleRunner(withInfo)
            .withArguments(commands.map { ":$forProjectName:$it" } + "--stacktrace")
            .build()
    }

    fun runGradleAndFail(
        forProjectName: String,
        commands: List<String>,
        withInfo: Boolean = false
    ): BuildResult {
        return createGradleRunner(withInfo)
            .withArguments(commands.map { ":$forProjectName:$it" } + "--stacktrace")
            .buildAndFail()
    }

    fun addSubproject(descriptor: ProjectDescriptor) {
        val projectDir = File(rootDir, descriptor.projectName).apply { mkdirs() }

        File(projectDir, BUILD_GRADLE_FILE_NAME).writeText(descriptor.getBuildGradleContents())
        createProjectManifestFile(projectDir)
        descriptor.projectDirBuilder.buildDirectory(projectDir)

        settingsFile.appendText("\ninclude '${descriptor.projectName}'\n")
    }

    private fun createGradleRunner(withInfo: Boolean): GradleRunner {
        val runner = GradleRunner.create()
            .withProjectDir(rootDir)
            .withPluginClasspath()

        return if (withInfo) {
            runner.withArguments("--info")
        } else {
            runner
        }
    }

    private fun createProjectManifestFile(parentDir: File) {
        val androidManifest = File(parentDir, "src/main/$ANDROID_MANIFEST_FILE_NAME")
        androidManifest.parentFile.mkdirs()
        androidManifest.writeText(
            """
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"/>
            """.trimIndent()
        )
    }

    private fun setUpRootProject() {
        rootGradleFile.writeText("")
        settingsFile.writeText(
            """
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                    google()
                }
            }
            dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    mavenCentral()
                    google()
                    gradlePluginPortal()
                }
            }
            rootProject.name = "sample-project"
            """.trimIndent()
        )
    }

    private companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
        private const val ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml"
    }
}
