plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.dija.skynetreader"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dija.skynetreader"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.foundation.android)
    implementation(libs.material)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.core.testing)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle & LiveData
    implementation(libs.androidx.lifecycle.runtime.ktx.v287)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Retrofit & Gson (Networking)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Image Loading (Choose one: Glide or Coil)
    implementation(libs.landscapist.glide) // Glide
    implementation(libs.coil.compose) // Alternative: Coil (comment out if not used)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room Database (Offline Storage)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)


    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    // UI Testing
    androidTestImplementation(libs.ui.test.junit4)

    // Robolectric for Unit Testing
    testImplementation(libs.robolectric)
}