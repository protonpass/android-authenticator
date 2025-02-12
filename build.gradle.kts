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
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.sentry) apply false
    alias(libs.plugins.proton.detekt)
}

protonDetekt {
    threshold = 0
}

val authenticatorAar = File(rootProject.projectDir, "libs/lib-release.aar")
val versionCatalog = extensions.findByType<VersionCatalogsExtension>()?.named("libs")
val authenticatorCommon = versionCatalog?.findLibrary("authenticator-common")?.get()?.get()

subprojects {
    afterEvaluate {
        configurations.configureEach {
            withDependencies {
                if (authenticatorCommon != null) {
                    val removed = removeIf { dependency ->
                        dependency.group == authenticatorCommon.module.group && dependency.name == authenticatorCommon.module.name
                    }
                    if (removed) {
                        if (authenticatorAar.exists()) {
                            val aarDependency = project.dependencies.create(files(authenticatorAar))
                            add(aarDependency)
                            logger.quiet("✅  Using AAR for ${authenticatorCommon.module}")
                        } else {
                            add(authenticatorCommon)
                        }
                    }
                }
            }
        }
    }
}