plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.likethesalad.android.aaper"
    compileSdk = 36

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
            }
        }
    }
}

dependencies {
    api(project(":aaper-api"))
    api(libs.androidx.startupRuntime)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit4)
    testRuntimeOnly(libs.junit.launcher)
    testRuntimeOnly(libs.junit.vintage)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
}
