import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("javalib-conventions")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

