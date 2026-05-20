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

import proton.android.authenticator.business.anonymous.data.domain.AnonymousData
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.users.domain.User
import proton.android.authenticator.protonapps.domain.ProtonApp
import proton.android.authenticator.shared.common.domain.configs.AppConfig
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
        private val user: User?,
        val appConfig: AppConfig
    ) : SettingsMasterState {

        internal val accountDisplayName: String? = user?.displayName

        internal val showExportOption: Boolean = configModel.canExportEntries

        internal val isVersionClickable: Boolean = configModel.buildFlavor.type.canDisplayDebugScreen

        internal val versionName: String = configModel.appVersionName

        internal val feedbackUrl: String = UrlConstants.CUSTOMER_SUPPORT

        internal val howToUrl: String = UrlConstants.HOW_TO

        internal val settingsModel: SettingsMasterSettingsModel = SettingsMasterSettingsModel(
            isSyncEnabled = settings.isSyncEnabled,
            isHideCodesEnabled = settings.isHideCodesEnabled,
            appLockType = settings.appLockType,
            isCodeChangeAnimationEnabled = settings.isCodeChangeAnimationEnabled,
            themeType = settings.themeType,
            searchBarType = settings.searchBarType,
            digitType = settings.digitType,
            sortingType = settings.sortingType,
            isPassBannerDismissed = settings.isPassBannerDismissed,
            isUndecryptableEntriesWarningDismissed = settings.isUndecryptableEntriesWarningDismissed,
            hasUndecryptableEntries = settings.hasUndecryptableEntries,
            isFirstRun = settings.isFirstRun,
            installationTime = settings.installationTime
        )

        internal val discoverModel: SettingsMasterDiscoverModel = SettingsMasterDiscoverModel(
            uninstalledProtonApps = uninstalledProtonApps,
            appConfig = appConfig
        )

        internal val bannerModel: SettingsMasterBannerModel = SettingsMasterBannerModel(
            isPassBannerDismissed = settings.isPassBannerDismissed,
            uninstalledProtonApps = uninstalledProtonApps,
            appConfig = appConfig
        )

        internal val anonymousData: AnonymousData? = configModel.anonymousData

    }

}
