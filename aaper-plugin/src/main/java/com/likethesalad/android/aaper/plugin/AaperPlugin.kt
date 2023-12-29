package com.likethesalad.android.aaper.plugin

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.likethesalad.android.aaper.plugin.appender.CodeAppenderTask
import com.likethesalad.android.aaper.plugin.instrumentation.target.TargetAsmClassVisitorFactory
import com.likethesalad.android.generated.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class AaperPlugin : Plugin<Project> {

    companion object {
        private const val ANDROID_APP_PLUGIN_ID = "com.android.application"
    }

    override fun apply(project: Project) {
        project.plugins.withId(ANDROID_APP_PLUGIN_ID) {
            validateAgpVersion(project)
            setUp(project)
        }
        checkProjectIsValid(project)
    }

    private fun validateAgpVersion(project: Project) {
        val agpVersion =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java).pluginVersion

        if (agpVersion < AndroidPluginVersion(7, 4, 0)) {
            throw GradleException("Aaper needs Android Gradle Plugin version >= 7.4.0 to work. The version available in this project is: $agpVersion")
        }
    }

    private fun setUp(project: Project) {
        setUpAndroidTransformation(project)
        addSdkDependency(project)
    }

    private fun checkProjectIsValid(project: Project) {
        project.afterEvaluate {
            if (!project.plugins.hasPlugin(ANDROID_APP_PLUGIN_ID)) {
                throw GradleException("No Android application found, Aaper must be added into an Android application project.")
            }
        }
    }

    private fun addSdkDependency(project: Project) {
        val localDependencies = System.getProperty("aaper_local_dependencies")
        if (localDependencies == null) {
            project.dependencies.add("implementation", BuildConfig.SDK_DEPENDENCY_URI)
        } else {
            localDependencies.split(",").forEach {
                project.dependencies.add("implementation", project.files(it))
            }
        }
    }

    private fun setUpAndroidTransformation(project: Project) {
        val componentsExtension =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

        componentsExtension.onVariants { variant ->
            val taskProvider = project.tasks.register(
                variant.name + "AaperCodeAppender",
                CodeAppenderTask::class.java
            )
            variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
                .use(taskProvider)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    CodeAppenderTask::allJars,
                    CodeAppenderTask::allDirectories,
                    CodeAppenderTask::outputFile
                )

            variant.instrumentation.transformClassesWith(
                TargetAsmClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}
        }
    }
}