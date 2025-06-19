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

rootProject.name = "ProtonAuthenticator"

pluginManagement {
    includeBuild("platform/build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("me.proton.core.gradle-plugins.include-core-build") version "1.3.1"
}

includeCoreBuild {
    branch.set("main")

    includeBuild("gopenpgp")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":business:app_lock")
include(":business:backups")
include(":business:biometrics")
include(":business:entries")
include(":business:entry_codes")
include(":business:proton_apps")
include(":business:settings")
include(":business:shared")
include(":business:steps")
include(":business:users")
include(":features:backups:master")
include(":features:exports:completion")
include(":features:exports:errors")
include(":features:home:manual")
include(":features:home:master")
include(":features:home:scan")
include(":features:imports:completion")
include(":features:imports:errors")
include(":features:imports:options")
include(":features:imports:passwords")
include(":features:imports:shared")
include(":features:onboarding:biometrics")
include(":features:onboarding:imports")
include(":features:onboarding:master")
include(":features:settings:master")
include(":features:shared")
include(":features:sync:disable")
include(":features:sync:master")
include(":navigation")
include(":shared:common")
include(":shared:crypto")
include(":shared:ui")
