/*
 * Copyright (c) 2025 Proton AG
 * This file is part of Proton AG and Proton Authenticator.
 *
 * Proton Authenticator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Authenticator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Authenticator.  If not, see <https://www.gnu.org/licenses/>.
 */

import configuration.extensions.protonEnvironment
import configuration.util.toBuildConfigValue
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.proton.environmentConfig)
    alias(libs.plugins.dependency.guard)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.sentry)
}

val privateProperties = Properties().apply {
    try {
        load(rootDir.resolve("private.properties").inputStream())
    } catch (exception: java.io.FileNotFoundException) {
        // Provide empty properties to allow the app to be built without secrets
        Properties()
    }
}

val sentryDSN: String? = System.getenv("SENTRY_DSN")

val jobId: Int = System.getenv("CI_JOB_ID")?.take(3)?.toInt() ?: 0
val appVersionName: String = "0.0.0"
val appVersionCode: Int = versionCode(appVersionName)

fun versionCode(versionName: String): Int {
    val segment = versionName.split('.').map { it.toInt() }
    return (segment[0] * 10000000) + (segment[1] * 100000) + (segment[2] * 1000) + jobId
}

android {
    namespace = "proton.android.authenticator"
    compileSdk = 35
    ndkVersion = "28.1.13356709"

    defaultConfig {
        applicationId = "proton.android.authenticator"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SENTRY_DSN", sentryDSN.toBuildConfigValue())
    }

    buildFeatures{
        buildConfig = true
    }

    lint {
        disable += "NullSafeMutableLiveData"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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

    sourceSets {
        all {
            if (!name.contains("fdroid", ignoreCase = true)) {
                kotlin.srcDir("src/nonFdroid/kotlin")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.material.navigation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.work)
    implementation(libs.authenticator.common)
    implementation(libs.coil)
    implementation(libs.core.crypto)
    implementation(libs.core.data)
    implementation(libs.core.dataRoom)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))

    implementation(projects.business.backups)
    implementation(projects.business.biometrics)
    implementation(projects.business.entries)
    implementation(projects.business.settings)
    implementation(projects.features.shared)
    implementation(projects.navigation)
    implementation(projects.shared.common)
    implementation(projects.shared.ui)

    addFdroidSpecialLib(
        default = libs.core.utilAndroidSentry,
        fdroid = null
    )

    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)

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

sentry {
    autoInstallation.enabled.set(false)
    ignoredBuildTypes.set(setOf("debug"))
    ignoredFlavors.set(setOf("fdroid"))
}

fun DependencyHandlerScope.addFdroidSpecialLib(
    default: Any,
    fdroid: Any?
) {
    val devImplementation = configurations.getByName("devImplementation")
    val alphaImplementation = configurations.getByName("alphaImplementation")
    val playImplementation = configurations.getByName("playImplementation")
    val fdroidImplementation = configurations.getByName("fdroidImplementation")

    devImplementation(default)
    alphaImplementation(default)
    playImplementation(default)

    fdroid?.let { dep ->
        fdroidImplementation(dep)
    }
}
