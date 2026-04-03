plugins {
    alias(libs.plugins.android.application)
    id("com.likethesalad.aaper")
}

android {
    namespace = "com.likethesalad.android.aaper.sample"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.likethesalad.android.aaper.sample"
        minSdk = 19
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx.appCompat)
    testImplementation(rootLibs.assertj)
    testImplementation(rootLibs.junit4)
    testImplementation(rootLibs.mockk)
    testImplementation(rootLibs.robolectric)
    debugImplementation(libs.fragmentTesting)
}
