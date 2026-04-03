package com.likethesalad.tools.functional.testing.android.descriptor

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.descriptor.ProjectDescriptor
import java.io.File

open class AndroidProjectDescriptor(
    name: String,
    inputDir: File,
    pluginId: String,
    private val blockItems: List<GradleBlockItem> = emptyList(),
    private val namespace: String = "some.localpackage.localname.forlocal.$name",
    private val compileSdkVersion: Int = 36
) : ProjectDescriptor(name, inputDir) {

    init {
        pluginsBlock.addPlugin(GradlePluginDeclaration(pluginId))
    }

    override fun getBuildGradleContents(): String {
        return """
            ${pluginsBlock.getItemText()}

            android {
                namespace = "$namespace"
                compileSdk $compileSdkVersion
            }

            ${placeBlockItems(blockItems)}

            ${dependenciesBlock.getItemText()}
            """.trimIndent()
    }
}
