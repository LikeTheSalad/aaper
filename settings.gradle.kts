pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.likethesalad.artifact-publisher") {
                useModule("com.likethesalad.tools:artifact-publisher:${requested.version}")
            }
            if (requested.id.namespace == "com.likethesalad.tools") {
                useModule("com.likethesalad.tools:plugin-tools:${requested.version}")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}
rootProject.name = "Annotated permissions"
include(":aaper-api")
include(":aaper")
include(":aaper-plugin")