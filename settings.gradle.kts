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
rootProject.name = "Annotated permissions"
include(":aaper-api")
include(":aaper")
include(":aaper-plugin")