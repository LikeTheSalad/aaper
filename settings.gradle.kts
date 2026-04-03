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
includeBuild("build-tools")
include(":aaper-api")
include(":aaper")
include(":aaper-plugin")