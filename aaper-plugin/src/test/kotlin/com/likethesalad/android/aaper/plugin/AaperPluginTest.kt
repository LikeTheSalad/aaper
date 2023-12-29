package com.likethesalad.android.aaper.plugin

import com.google.common.truth.Truth.assertThat
import com.likethesalad.tools.functional.testing.AndroidTestProject
import com.likethesalad.tools.functional.testing.android.descriptor.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AaperPluginTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val projectDirProvider = TestAssetsProvider("projects")

    @Test
    fun `Verify all annotated methods return VOID`() {
        val projectName = "nonVoidMethod"
        val testProject = AndroidTestProject(temporaryFolder.root)
        val descriptor = createProjectDescriptor(projectName)

        testProject.addSubproject(descriptor)

        val buildResult = testProject.runGradleAndFail(projectName, "assembleDebug")

        assertThat(buildResult.output).contains("EnsurePermissions-annotated methods must return VOID, but the method 'someMethod' inside 'com.example.SomeActivity' returns 'int' instead.")
    }

    private fun createProjectDescriptor(projectName: String): AndroidAppProjectDescriptor {
        val descriptor = AndroidAppProjectDescriptor(
            projectName,
            projectDirProvider.getFile(projectName),
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.aaper"))

        return descriptor
    }
}