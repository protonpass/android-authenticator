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

package proton.android.authenticator.features.settings.master.presentation

import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.users.domain.User
import proton.android.authenticator.protonapps.domain.ProtonApp
import proton.android.authenticator.shared.common.domain.builds.BuildFlavorType
import proton.android.authenticator.shared.common.domain.constants.UrlConstants

internal sealed interface SettingsMasterState {

    val event: SettingsMasterEvent

    data object Loading : SettingsMasterState {

        override val event: SettingsMasterEvent = SettingsMasterEvent.Idle

    }

    data class Ready(
        override val event: SettingsMasterEvent,
        private val configModel: SettingsMasterConfigModel,
        private val settings: Settings,
        private val uninstalledProtonApps: List<ProtonApp>,
        private val user: User?
    ) : SettingsMasterState {

        internal val accountDisplayName: String? = user?.displayName

        internal val isVersionClickable: Boolean = when (configModel.buildFlavor.type) {
            BuildFlavorType.Alpha,
            BuildFlavorType.Dev -> true

            BuildFlavorType.Fdroid,
            BuildFlavorType.PlayStore -> false
        }

        internal val versionName: String = configModel.appVersionName

        internal val feedbackUrl: String = UrlConstants.CUSTOMER_SUPPORT

        internal val howToUrl: String = UrlConstants.HOW_TO

        internal val settingsModel = SettingsMasterSettingsModel(
            isSyncEnabled = settings.isSyncEnabled,
            isHideCodesEnabled = settings.isHideCodesEnabled,
            appLockType = settings.appLockType,
            isCodeChangeAnimationEnabled = settings.isCodeChangeAnimationEnabled,
            themeType = settings.themeType,
            searchBarType = settings.searchBarType,
            digitType = settings.digitType,
            isPassBannerDismissed = settings.isPassBannerDismissed,
            isFirstRun = settings.isFirstRun,
            installationTime = settings.installationTime
        )

        internal val discoverModel = SettingsMasterDiscoverModel(
            uninstalledProtonApps = uninstalledProtonApps
        )

        internal val bannerModel = SettingsMasterBannerModel(
            isPassBannerDismissed = settings.isPassBannerDismissed,
            uninstalledProtonApps = uninstalledProtonApps
        )
    }

}
