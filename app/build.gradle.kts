import configuration.extensions.protonEnvironment
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.proton.environmentConfig)
    alias(libs.plugins.dependency.guard)
}

val privateProperties = Properties().apply {
    try {
        load(rootDir.resolve("private.properties").inputStream())
    } catch (exception: java.io.FileNotFoundException) {
        // Provide empty properties to allow the app to be built without secrets
        Properties()
    }
}

val jobId: Int = System.getenv("CI_JOB_ID")?.take(3)?.toInt() ?: 0
val appVersionName: String = "0.0.0"
val appVersionCode: Int = versionCode(appVersionName)

fun versionCode(versionName: String): Int {
    val segment = versionName.split('.').map { it.toInt() }
    return (segment[0] * 10000000) + (segment[1] * 100000) + (segment[2] * 1000) + jobId
}

android {
    namespace = "proton.android.authenticator"
    compileSdk = 34

    defaultConfig {
        applicationId = "proton.android.authenticator"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        register("signingKeystore") {
            storeFile = file("$rootDir/keystore/ProtonMail.keystore")
            storePassword = "${privateProperties["keyStorePassword"]}"
            keyAlias = "ProtonMail"
            keyPassword = "${privateProperties["keyStoreKeyPassword"]}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["signingKeystore"]
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

    flavorDimensions += "version"
    productFlavors {
        create("dev") {
            dimension = "version"
            isDefault = true
            resourceConfigurations.addAll(listOf("en", "xxhdpi"))
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("alpha") {
            dimension = "version"
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha.$appVersionCode"
        }
        create("play") {
            dimension = "version"
        }
        create("fdroid") {
            dimension = "version"
            applicationIdSuffix = ".fdroid"
        }
    }

    flavorDimensions += "env"
    productFlavors {
        create("black") {
            dimension = "env"
            applicationIdSuffix = ".black"

            protonEnvironment {
                host = "proton.black"
            }
        }
        create("prod") {
            dimension = "env"
            protonEnvironment {
                useDefaultPins = true
                apiPrefix = "pass-api"
            }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

dependencyGuard {
    configuration("playProdReleaseRuntimeClasspath") {
        artifacts = true
        modules = false

        allowedFilter = {
            !it.contains("junit")
        }
    }
    configuration("fdroidProdReleaseRuntimeClasspath") {
        artifacts = true
        modules = false

        allowedFilter = {
            !it.contains("junit")
            !it.contains("com.android.billingclient")
            !it.contains("com.google.android.gms")
            !it.contains("com.google.android.play")
            !it.contains("io.sentry")
        }
    }
}