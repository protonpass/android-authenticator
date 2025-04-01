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

internal class SettingsMasterState private constructor(
    internal val settingsModel: SettingsMasterSettingsModel,
    internal val discoverModel: SettingsMasterDiscoverModel,
    internal val bannerModel: SettingsMasterBannerModel
) {

    internal companion object {

        @Composable
        internal fun create(
            settingsFlow: Flow<Settings>,
            uninstalledProtonAppsFlow: Flow<List<ProtonApp>>
        ): SettingsMasterState {
            val settings by settingsFlow.collectAsState(initial = Settings.Default)
            val uninstalledProtonApps by uninstalledProtonAppsFlow.collectAsState(initial = emptyList())

            val settingsModel = remember(key1 = settings) {
                SettingsMasterSettingsModel(
                    isBackupEnabled = settings.isBackupEnabled,
                    isSyncEnabled = settings.isSyncEnabled,
                    isTapToRevealEnabled = settings.isTapToRevealEnabled,
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
                bannerModel = bannerModel
            )
        }
    }

}
