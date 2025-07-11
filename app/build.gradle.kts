plugins {
    // Your other app plugins (e.g., com.android.application, kotlin-android)
    alias(libs.plugins.android.application) // Or id("com.android.application")
    alias(libs.plugins.googleServices)     // Or id("com.google.gms.google-services")
}

android {
    namespace = "com.example.immunobuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.immunobuddy"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.junit)
    implementation("com.google.android.gms:play-services-auth:21.0.0")

}