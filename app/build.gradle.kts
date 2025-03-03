import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.mobilesecurityproject"
    compileSdk = 35


    // ✅ Load API key from local.properties
    val localProperties = Properties()
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        FileInputStream(localFile).use { localProperties.load(it) }
    }
    val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""


    defaultConfig {
        applicationId = "com.example.mobilesecurityproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Store API key in BuildConfig
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")

        // ✅ Pass API Key to Manifest Placeholder
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

    //Added by me:
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    //Google Maps:
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    //Glide:
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    //Lottie:
    implementation(libs.lottie)
}



