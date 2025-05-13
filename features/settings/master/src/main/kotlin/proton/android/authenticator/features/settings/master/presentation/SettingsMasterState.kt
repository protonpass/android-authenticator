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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.protonapps.domain.ProtonApp
import proton.android.authenticator.shared.common.domain.models.MimeType

internal class SettingsMasterState private constructor(
    internal val settingsModel: SettingsMasterSettingsModel,
    internal val discoverModel: SettingsMasterDiscoverModel,
    internal val bannerModel: SettingsMasterBannerModel,
    internal val event: SettingsMasterEvent
) {

    internal val exportFileName: String = FILE_NAME

    internal val exportFileMimeType: String = MimeType.Json.value

    internal val feedbackUrl: String = URL_FEEDBACK

    internal val howToUrl: String = URL_HOW_TO

    internal companion object {

        private const val FILE_NAME = "proton_authenticator_backup.json"

        private const val URL_FEEDBACK = "https://proton.me/support/contact"

        private const val URL_HOW_TO = "https://proton.me/support/contact"

        @Composable
        internal fun create(
            settingsFlow: Flow<Settings>,
            uninstalledProtonAppsFlow: Flow<List<ProtonApp>>,
            eventFlow: Flow<SettingsMasterEvent>
        ): SettingsMasterState {
            val settings by settingsFlow.collectAsState(initial = Settings.Default)
            val uninstalledProtonApps by uninstalledProtonAppsFlow.collectAsState(initial = emptyList())
            val event by eventFlow.collectAsState(initial = SettingsMasterEvent.Idle)

            val settingsModel = remember(key1 = settings) {
                SettingsMasterSettingsModel(
                    isBackupEnabled = settings.isBackupEnabled,
                    isSyncEnabled = settings.isSyncEnabled,
                    isHideCodesEnabled = settings.isHideCodesEnabled,
                    appLockType = settings.appLockType,
                    isCodeChangeAnimationEnabled = settings.isCodeChangeAnimationEnabled,
                    themeType = settings.themeType,
                    searchBarType = settings.searchBarType,
                    digitType = settings.digitType,
                    isPassBannerDismissed = settings.isPassBannerDismissed
                )
            }

            val discoverModel = remember(key1 = uninstalledProtonApps) {
                SettingsMasterDiscoverModel(
                    uninstalledProtonApps = uninstalledProtonApps
                )
            }

            val bannerModel = remember(
                key1 = settings.isPassBannerDismissed,
                key2 = uninstalledProtonApps
            ) {
                SettingsMasterBannerModel(
                    isPassBannerDismissed = settings.isPassBannerDismissed,
                    uninstalledProtonApps = uninstalledProtonApps
                )
            }

            return SettingsMasterState(
                settingsModel = settingsModel,
                discoverModel = discoverModel,
                bannerModel = bannerModel,
                event = event
            )
        }
    }

}
