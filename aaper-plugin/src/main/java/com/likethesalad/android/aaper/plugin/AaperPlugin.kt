package com.likethesalad.android.aaper.plugin

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.likethesalad.android.aaper.plugin.instrumentation.AaperAsmClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class AaperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            setUp(project)
        }
    }

    private fun setUp(project: Project) {
        val componentsExtension =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

        componentsExtension.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                AaperAsmClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) { }
        }
    }
}