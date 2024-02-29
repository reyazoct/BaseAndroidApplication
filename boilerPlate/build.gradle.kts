import java.net.URI

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.reyaz"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("maven") {
                groupId = "com.reyaz"
                artifactId = "boilerPlate"
                version = "0.1"

                afterEvaluate {
                    from(components["release"])
                }

                pom {
                    name = "Compose Boiler Plate"
                    description = "A library to use use boiler plate code directly"
                }
            }
        }

        val spaceUserName: String by project
        val spacePassword: String by project

        repositories {
            maven {
                credentials {
                    username = spaceUserName
                    password = spacePassword
                }

                url = URI("https://maven.pkg.jetbrains.space/reyaz/p/main/boiler-plate")
            }
        }
    }
}

