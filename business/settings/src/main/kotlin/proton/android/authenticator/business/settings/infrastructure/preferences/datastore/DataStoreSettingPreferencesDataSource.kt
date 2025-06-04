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

package proton.android.authenticator.business.settings.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.proto.preferences.settings.SettingsPreferences
import proton.android.authenticator.proto.preferences.settings.SettingsPreferencesAppLockType
import proton.android.authenticator.proto.preferences.settings.SettingsPreferencesDigitType
import proton.android.authenticator.proto.preferences.settings.SettingsPreferencesSearchBarType
import proton.android.authenticator.proto.preferences.settings.SettingsPreferencesThemeType
import javax.inject.Inject

internal class DataStoreSettingPreferencesDataSource @Inject constructor(
    private val settingsPreferencesDataStore: DataStore<SettingsPreferences>
) : PreferencesDataSource<Settings> {

    override fun observe(): Flow<Settings> = settingsPreferencesDataStore.data
        .map { settingsPreferences ->
            Settings(
                isBackupEnabled = settingsPreferences.isBackupEnabled,
                isSyncEnabled = settingsPreferences.isSyncEnabled,
                appLockType = settingsPreferences.appLockType.toDomain(),
                isHideCodesEnabled = settingsPreferences.isHideCodesEnabled,
                themeType = settingsPreferences.themeType.toDomain(),
                searchBarType = settingsPreferences.searchBarType.toDomain(),
                digitType = settingsPreferences.digitType.toDomain(),
                isCodeChangeAnimationEnabled = settingsPreferences.isCodeChangeAnimationEnabled,
                isPassBannerDismissed = settingsPreferences.isPassBannerDismissed
            )
        }

    override suspend fun update(settings: Settings) {
        settingsPreferencesDataStore.updateData { settingsPreferences ->
            settingsPreferences.toBuilder()
                .setIsBackupEnabled(settings.isBackupEnabled)
                .setIsSyncEnabled(settings.isSyncEnabled)
                .setAppLockType(settings.appLockType.toPreferences())
                .setIsHideCodesEnabled(settings.isHideCodesEnabled)
                .setThemeType(settings.themeType.toPreferences())
                .setSearchBarType(settings.searchBarType.toPreferences())
                .setDigitType(settings.digitType.toPreferences())
                .setIsCodeChangeAnimationEnabled(settings.isCodeChangeAnimationEnabled)
                .setIsPassBannerDismissed(settings.isPassBannerDismissed)
                .build()
        }
    }

    private fun SettingsPreferencesAppLockType.toDomain() = when (this) {
        SettingsPreferencesAppLockType.SETTINGS_APP_LOCK_TYPE_BIOMETRIC -> SettingsAppLockType.Biometric
        SettingsPreferencesAppLockType.SETTINGS_APP_LOCK_TYPE_NONE,
        SettingsPreferencesAppLockType.UNRECOGNIZED -> SettingsAppLockType.None
    }

    private fun SettingsAppLockType.toPreferences() = when (this) {
        SettingsAppLockType.None -> SettingsPreferencesAppLockType.SETTINGS_APP_LOCK_TYPE_NONE
        SettingsAppLockType.Biometric -> SettingsPreferencesAppLockType.SETTINGS_APP_LOCK_TYPE_BIOMETRIC
    }

    private fun SettingsPreferencesThemeType.toDomain() = when (this) {
        SettingsPreferencesThemeType.SETTING_THEME_TYPE_LIGHT -> SettingsThemeType.Light
        SettingsPreferencesThemeType.SETTING_THEME_TYPE_DARK -> SettingsThemeType.Dark
        SettingsPreferencesThemeType.SETTING_THEME_TYPE_SYSTEM,
        SettingsPreferencesThemeType.UNRECOGNIZED -> SettingsThemeType.System
    }

    private fun SettingsThemeType.toPreferences() = when (this) {
        SettingsThemeType.System -> SettingsPreferencesThemeType.SETTING_THEME_TYPE_SYSTEM
        SettingsThemeType.Light -> SettingsPreferencesThemeType.SETTING_THEME_TYPE_LIGHT
        SettingsThemeType.Dark -> SettingsPreferencesThemeType.SETTING_THEME_TYPE_DARK
    }

    private fun SettingsPreferencesSearchBarType.toDomain() = when (this) {
        SettingsPreferencesSearchBarType.SETTINGS_SEARCH_BAR_TYPE_TOP -> SettingsSearchBarType.Top
        SettingsPreferencesSearchBarType.SETTINGS_SEARCH_BAR_TYPE_BOTTOM,
        SettingsPreferencesSearchBarType.UNRECOGNIZED -> SettingsSearchBarType.Bottom
    }

    private fun SettingsSearchBarType.toPreferences() = when (this) {
        SettingsSearchBarType.Bottom -> SettingsPreferencesSearchBarType.SETTINGS_SEARCH_BAR_TYPE_BOTTOM
        SettingsSearchBarType.Top -> SettingsPreferencesSearchBarType.SETTINGS_SEARCH_BAR_TYPE_TOP
    }

    private fun SettingsPreferencesDigitType.toDomain() = when (this) {
        SettingsPreferencesDigitType.SETTINGS_DIGIT_TYPE_BOXES -> SettingsDigitType.Boxes
        SettingsPreferencesDigitType.SETTINGS_DIGIT_TYPE_PLAIN,
        SettingsPreferencesDigitType.UNRECOGNIZED -> SettingsDigitType.Plain
    }

    private fun SettingsDigitType.toPreferences() = when (this) {
        SettingsDigitType.Boxes -> SettingsPreferencesDigitType.SETTINGS_DIGIT_TYPE_BOXES
        SettingsDigitType.Plain -> SettingsPreferencesDigitType.SETTINGS_DIGIT_TYPE_PLAIN
    }

}
