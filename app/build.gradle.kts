import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val GOOGLE_MAP_API = (properties.getProperty("GOOGLE_MAP_API")).replace("\"", "")
val EMERGENCY_ROOM_API = (properties.getProperty("EMERGENCY_ROOM_API"))

android {
    namespace = "com.example.find_emergencyroom_composable"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.find_emergencyroom_composable"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        addManifestPlaceholders(mapOf("GOOGLE_MAP_API" to GOOGLE_MAP_API))

        buildConfigField("String", "EMERGENCY_ROOM_API", EMERGENCY_ROOM_API)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            buildConfig = true
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.1"
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.navigation.compose)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        implementation(libs.play.services.maps)
        implementation(libs.play.services.maps.v1810)
        implementation(libs.play.services.location)
        implementation(libs.maps.compose.v290)
        implementation(libs.accompanist.permissions)
        implementation(libs.kotlinx.coroutines.play.services)
        implementation(libs.gms.play.services.location)
        implementation(libs.play.services.location.vplayserviceslocation)
    }
}
