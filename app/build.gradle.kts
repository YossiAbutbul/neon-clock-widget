plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neonclock.widget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.neonclock.widget"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }

    // Fixed debug key in the repo, so every new build installs over the previous one
    signingConfigs {
        getByName("debug") {
            storeFile = file("bigclock-debug.keystore")
            storePassword = "android"
            keyAlias = "bigclock"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug { signingConfig = signingConfigs.getByName("debug") }
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
