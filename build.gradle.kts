plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
