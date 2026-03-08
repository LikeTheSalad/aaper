plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.likethesalad.aaper")
}

android {
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
    testImplementation(rootLibs.unitTesting)
    testImplementation(rootLibs.robolectric)
    debugImplementation(libs.fragmentTesting)
}
