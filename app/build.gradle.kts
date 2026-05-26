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

import com.android.build.api.dsl.VariantDimension
import configuration.extensions.protonEnvironment
import configuration.util.toBuildConfigValue
import proton.android.authenticator.platform.buildlogic.domain.platform.configuration.PlatformAndroidConfig
import java.util.Properties

plugins {
    id("proton.android.authenticator.plugins.applications.authenticator")
}

val privateProperties = Properties().apply {
    try {
        load(rootDir.resolve("private.properties").inputStream())
    } catch (exception: java.io.FileNotFoundException) {
        Properties()
    }
}

val sentryDSN: String? = System.getenv("SENTRY_DSN")
val atlasProxyToken: String? = privateProperties.getProperty("PROXY_TOKEN")

fun VariantDimension.setAssetLinksResValue(host: String) {
    resValue(
        type = "string", name = "asset_statements",
        value = """
            [{
              "relation": ["delegate_permission/common.handle_all_urls", "delegate_permission/common.get_login_creds"],
              "target": { "namespace": "web", "site": "https://$host" }
            }]
        """.trimIndent()
    )
}

android {
    defaultConfig {
        buildConfigField("String", "SENTRY_DSN", sentryDSN.toBuildConfigValue())
        setAssetLinksResValue("proton.me")

        protonEnvironment {
            proxyToken = atlasProxyToken
            apiPrefix = "authenticator-api"
        }
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
            versionNameSuffix = "-alpha.${PlatformAndroidConfig.getVersionCode()}"
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
                apiPrefix = "authenticator-api"
            }
        }
        create("prod") {
            dimension = "env"
            protonEnvironment {
                host = host
                apiPrefix = "authenticator-api"
                useDefaultPins = true
            }
        }
    }

    sourceSets {
        getByName("dev") {
            kotlin.srcDir("src/nonFdroid/kotlin")
        }
        getByName("alpha") {
            kotlin.srcDir("src/nonFdroid/kotlin")
        }
        getByName("play") {
            kotlin.srcDir("src/nonFdroid/kotlin")
        }
    }
}

androidComponents {
    beforeVariants { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(libs.core.accountManager)
    implementation(libs.core.auth)
    implementation(libs.core.authFidoDagger)
    implementation(libs.core.authFidoDomain)
    implementation(libs.core.crypto)
    implementation(libs.core.cryptoValidator)
    implementation(libs.core.data)
    implementation(libs.core.dataRoom)
    implementation(libs.core.featureFlag)
    implementation(libs.core.featureFlag.domain)
    implementation(libs.core.humanVerification)
    implementation(libs.core.notification)
    implementation(libs.core.push)
    implementation(libs.core.user)
    implementation(libs.core.userRecovery)
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
    implementation(projects.business.shared)
    implementation(projects.business.users)
    implementation(projects.features.shared)
    implementation(projects.features.unlock.master)
    implementation(projects.navigation)
    implementation(projects.shared.common)
    implementation(projects.shared.ui)

    addDevBlackImplementation(
        default = libs.core.config.dagger.staticDefaults,
        devBlack = libs.core.config.dagger.contentProvider
    )

    addFdroidSpecialLib(
        default = libs.core.authFidoPlay,
        fdroid = null
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

    addFdroidSpecialLib(
        default = libs.core.telemetry.data,
        fdroid = null
    )

    testImplementation(libs.junit)

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