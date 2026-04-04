package com.likethesalad.android.aaper.plugin

import com.likethesalad.tools.functional.testing.AndroidTestProject
import com.likethesalad.tools.functional.testing.android.descriptor.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class AaperPluginTest {

    @TempDir
    lateinit var temporaryFolder: File

    private val projectDirProvider = TestAssetsProvider("projects")

    @Test
    fun `Verify compilation issue if some methods don't return void`() {
        val projectName = "nonVoidMethod"
        val testProject = AndroidTestProject(temporaryFolder)
        val descriptor = createProjectDescriptor(projectName)

        testProject.addSubproject(descriptor)

        val buildResult = testProject.runGradleAndFail(projectName, "assembleDebug")

        assertThat(buildResult.output).contains("EnsurePermissions-annotated methods must return VOID, but the method 'someMethod' inside 'com.example.SomeActivity' returns 'int' instead.")
    }

    @Test
    fun `Verify no issues when all annotated methods return void`() {
        val projectName = "voidMethod"
        val testProject = AndroidTestProject(temporaryFolder)
        val descriptor = createProjectDescriptor(projectName)

        testProject.addSubproject(descriptor)

        testProject.runGradle(projectName, "assembleDebug")
    }

    @Test
    fun `Verify CodeAppenderTask loads from cache`() {
        val assetName = "voidMethod"
        val projectName = "voidMethod_cached"
        val cacheDir = File(temporaryFolder, "build-cache")
        val testProject = AndroidTestProject(temporaryFolder, cacheDir)
        val descriptor = createProjectDescriptor(assetName, projectName)

        testProject.addSubproject(descriptor)

        // First run: populates the cache
        testProject.runGradle(projectName, "assembleDebug")

        // Wipe build outputs to force a cache look-up on the next run
        File(temporaryFolder, "$projectName/build").deleteRecursively()

        // Second run: CodeAppenderTask should be restored FROM-CACHE
        val result = testProject.runGradle(projectName, "assembleDebug")

        assertThat(result.output).contains("> Task :$projectName:debugAaperCodeAppender FROM-CACHE")
    }

    private fun createProjectDescriptor(
        assetName: String,
        projectName: String = assetName
    ): AndroidAppProjectDescriptor {
        val descriptor = AndroidAppProjectDescriptor(
            projectName,
            projectDirProvider.getFile(assetName),
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.aaper"))

        return descriptor
    }
}
