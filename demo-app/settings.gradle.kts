import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    val rootProperties = java.util.Properties()
    val propertiesFile = File(rootDir, "../gradle.properties")
    propertiesFile.inputStream().use { rootProperties.load(it) }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.likethesalad.aaper") {
                useModule("com.likethesalad.android:aaper-plugin:${rootProperties["version"]}")
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
    versionCatalogs {
        create("rootLibs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
rootProject.name = "Annotated permissions sample app"
