import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("javalib-conventions")
    id("java-gradle-plugin")
    alias(libs.plugins.buildConfig)
}

val testLocalDependency by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = false
}
val testLocalClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(testLocalDependency)
}
val testPluginDependency by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = false
}
val testPluginClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(testPluginDependency)
}

tasks.withType<PluginUnderTestMetadata> {
    pluginClasspath.from(testPluginClasspath)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

dependencies {
    implementation(project(":aaper-api"))
    implementation(libs.bundles.asm)
    implementation(libs.classGraph)
    compileOnly(libs.android.gradle.plugin)
    testImplementation(gradleTestKit())
    testLocalDependency(project(":aaper-api")) {
        isTransitive = false
    }
    testPluginDependency(libs.android.gradle.plugin)
}

tasks.withType<Test> {
    systemProperty("aaper_local_dependencies", testLocalClasspath.files.joinToString(","))
}

buildConfig {
    packageName("${group}.generated")
    buildConfigField("String", "SDK_DEPENDENCY_URI", "\"$group:aaper:$version\"")
}

gradlePlugin {
    plugins {
        create("aaperPlugin") {
            id = "com.likethesalad.aaper"
            implementationClass = "com.likethesalad.android.aaper.plugin.AaperPlugin"
        }
    }
}
