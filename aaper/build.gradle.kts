plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.likethesalad.android.aaper"
    compileSdk = 36

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(project(":aaper-api"))
    api(libs.androidx.startupRuntime)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    testImplementation(libs.unitTesting)
    testImplementation(libs.robolectric)
}
