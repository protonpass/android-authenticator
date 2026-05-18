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

plugins {
    id("proton.android.authenticator.plugins.libraries.android")
    id("app.cash.paparazzi")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "proton.android.authenticator.screenshottests"

    buildFeatures {
        compose = true
    }

    sourceSets {
        getByName("test").java.srcDirs("build/generated/ksp/debugUnitTest/kotlin")
    }
}

androidComponents {
    beforeVariants(selector().withBuildType("release")) { builder ->
        builder.enable = false
    }
    beforeVariants { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(projects.features.backups.master)
    implementation(projects.features.backups.passwords)
    implementation(projects.features.exports.passwords)
    implementation(projects.features.home.master)
    implementation(projects.features.home.permissions)
    implementation(projects.features.home.scan)
    implementation(projects.features.imports.menus)
    implementation(projects.features.imports.onboarding)
    implementation(projects.features.imports.options)
    implementation(projects.features.imports.passwords)
    implementation(projects.features.imports.permissions)
    implementation(projects.features.imports.scan)
    implementation(projects.features.logs.master)
    implementation(projects.features.onboarding.biometrics)
    implementation(projects.features.onboarding.imports)
    implementation(projects.features.onboarding.master)
    implementation(projects.features.sync.master)
    implementation(projects.features.unlock.master)

    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.activity.compose)
    testImplementation(libs.androidx.compose.foundation)
    testImplementation(libs.androidx.ui)
    testImplementation(libs.androidx.ui.tooling)
    testImplementation(libs.androidx.ui.tooling.preview)
    testImplementation(projects.shared.ui)

    testImplementation(libs.showkase)
    kspTest(libs.showkase.processor)

    testImplementation(libs.test.parameter.injector)
}
