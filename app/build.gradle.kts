plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.fastgallery"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fastgallery"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            }
        }
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // ConstraintLayout for XML layout
    implementation(libs.androidx.constraintlayout)

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
}
