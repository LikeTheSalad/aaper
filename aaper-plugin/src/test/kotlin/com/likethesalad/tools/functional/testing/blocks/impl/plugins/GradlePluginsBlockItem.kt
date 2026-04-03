package com.likethesalad.tools.functional.testing.blocks.impl.plugins

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem

class GradlePluginsBlockItem : GradleBlockItem {
    private val plugins = mutableListOf<GradlePluginDeclaration>()

    override fun getItemText(): String {
        return """
            plugins {
                ${plugins.joinToString("\n") { convertToStringDeclaration(it) }}
            }
        """.trimIndent()
    }

    fun addPlugin(plugin: GradlePluginDeclaration) {
        plugins.add(plugin)
    }

    private fun convertToStringDeclaration(declaration: GradlePluginDeclaration): String {
        var result = "id '${declaration.id}'"
        declaration.version?.let { version ->
            result += " version '$version'"
        }
        return result
    }
}
