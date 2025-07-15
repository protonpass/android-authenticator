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

plugins {
    id("proton.android.authenticator.plugins.applications.authenticator")
}

val sentryDSN: String? = System.getenv("SENTRY_DSN")

val jobId: Int = System.getenv("CI_JOB_ID")?.take(3)?.toInt() ?: 0
val appVersionName: String = "0.0.4"
val appVersionCode: Int = versionCode(appVersionName)

fun versionCode(versionName: String): Int {
    val segment = versionName.split('.').map { it.toInt() }
    return (segment[0] * 10000000) + (segment[1] * 100000) + (segment[2] * 1000) + jobId
}

android {
    defaultConfig {
        buildConfigField("String", "SENTRY_DSN", sentryDSN.toBuildConfigValue())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
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
                apiPrefix = "authenticator"
                host = "proton.black/api"
            }
        }
        create("prod") {
            dimension = "env"
            protonEnvironment {
                apiPrefix = "authenticator-api"
                host = host
                useDefaultPins = true
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
    implementation(files("../../proton-libs/gopenpgp/gopenpgp.aar"))

    implementation(libs.core.accountManager)
    implementation(libs.core.auth)
    implementation(libs.core.crypto)
    implementation(libs.core.data)
    implementation(libs.core.dataRoom)
    implementation(libs.core.notification)
    implementation(libs.core.push)
    implementation(libs.core.user)
    implementation(libs.core.userSettings)
    implementation(libs.kotlinx.datetime)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))

    implementation(projects.business.appLock)
    implementation(projects.business.backups)
    implementation(projects.business.biometrics)
    implementation(projects.business.entries)
    implementation(projects.business.keys)
    implementation(projects.business.settings)
    implementation(projects.business.users)
    implementation(projects.features.shared)
    implementation(projects.navigation)
    implementation(projects.shared.common)
    implementation(projects.shared.ui)

    addDevBlackImplementation(
        default = libs.core.config.dagger.staticDefaults,
        devBlack = libs.core.config.dagger.contentProvider
    )

    addFdroidSpecialLib(
        default = libs.core.utilAndroidSentry,
        fdroid = null
    )

    addFdroidSpecialLib(
        default = libs.play.review,
        fdroid = null
    )

    addFdroidSpecialLib(
        default = libs.play.review.ktx,
        fdroid = null
    )

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

fun DependencyHandlerScope.addDevBlackImplementation(
    default: Any,
    devBlack: Any,
) {
    val devBlackImplementation = configurations.maybeCreate("devBlackImplementation")
    val devProdImplementation = configurations.maybeCreate("devProdImplementation")
    val alphaImplementation = configurations.getByName("alphaImplementation")
    val playImplementation = configurations.getByName("playImplementation")
    val fdroidImplementation = configurations.getByName("fdroidImplementation")

    devBlackImplementation(devBlack)
    devProdImplementation(default)
    alphaImplementation(default)
    playImplementation(default)
    fdroidImplementation(default)
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

play {
    serviceAccountCredentials.set(file("/tmp/play-service-account.json"))
    track.set("internal")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
    artifactDir.set(file("$rootDir/signedArtifacts"))
}