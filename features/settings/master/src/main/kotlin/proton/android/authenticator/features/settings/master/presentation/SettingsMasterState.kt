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
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.settings.domain.Settings

internal class SettingsMasterState private constructor(
    internal val settingsModel: SettingsMasterSettingsModel
) {

    internal companion object {

        @Composable
        internal fun create(settingsFlow: Flow<Settings>): SettingsMasterState {
            val settings by settingsFlow.collectAsState(initial = Settings.Default)

            return SettingsMasterState(
                settingsModel = SettingsMasterSettingsModel(
                    isBackupEnabled = settings.isBackupEnabled,
                    isSyncEnabled = settings.isSyncEnabled,
                    isTapToRevealEnabled = settings.isTapToRevealEnabled,
                    appLockType = settings.appLockType,
                    isCodeChangeAnimationEnabled = settings.isCodeChangeAnimationEnabled,
                    themeType = settings.themeType,
                    searchBarType = settings.searchBarType,
                    digitType = settings.digitType
                )
            )
        }
    }

}
