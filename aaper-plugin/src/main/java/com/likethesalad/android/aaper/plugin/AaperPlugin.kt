package com.likethesalad.android.aaper.plugin

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.likethesalad.android.aaper.plugin.instrumentation.generated.GeneratedAsmClassVisitorFactory
import com.likethesalad.android.aaper.plugin.instrumentation.target.TargetAsmClassVisitorFactory
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
                TargetAsmClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) { }
            variant.instrumentation.transformClassesWith(
                GeneratedAsmClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}
        }
    }
}