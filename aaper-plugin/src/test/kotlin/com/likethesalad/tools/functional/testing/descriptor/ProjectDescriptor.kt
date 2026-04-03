package com.likethesalad.tools.functional.testing.descriptor

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem
import com.likethesalad.tools.functional.testing.blocks.impl.GradleDependenciesBlockItem
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginsBlockItem
import com.likethesalad.tools.functional.testing.content.ProjectDirBuilder
import java.io.File

abstract class ProjectDescriptor(
    val projectName: String,
    inputDir: File
) {
    val projectDirBuilder = ProjectDirBuilder(inputDir)
    val pluginsBlock = GradlePluginsBlockItem()
    val dependenciesBlock = GradleDependenciesBlockItem()

    abstract fun getBuildGradleContents(): String

    protected fun placeBlockItems(blockItems: List<GradleBlockItem>): String {
        if (blockItems.isEmpty()) {
            return ""
        }

        return blockItems.joinToString("\n") { it.getItemText() }
    }
}
