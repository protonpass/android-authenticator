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
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType

internal data class SettingsMasterSettingsModel(
    internal val isSyncEnabled: Boolean,
    internal val appLockType: SettingsAppLockType,
    internal val isHideCodesEnabled: Boolean,
    internal val themeType: SettingsThemeType,
    internal val searchBarType: SettingsSearchBarType,
    internal val digitType: SettingsDigitType,
    internal val isCodeChangeAnimationEnabled: Boolean,
    internal val isPassBannerDismissed: Boolean,
    internal val installationTime: Long
) {

    internal val appLockOptions: List<SettingsMasterAppLockOption> = listOf(
        SettingsMasterAppLockOption.None(selectedType = appLockType),
        SettingsMasterAppLockOption.Biometric(selectedType = appLockType)
    )

    internal val themeOptions: List<SettingsMasterThemeOption> = listOf(
        SettingsMasterThemeOption.System(selectedType = themeType),
        SettingsMasterThemeOption.Light(selectedType = themeType),
        SettingsMasterThemeOption.Dark(selectedType = themeType)
    )

    internal val searchBarOptions: List<SettingsMasterSearchBarOption> = listOf(
        SettingsMasterSearchBarOption.Bottom(selectedType = searchBarType),
        SettingsMasterSearchBarOption.Top(selectedType = searchBarType)
    )

    internal val digitOptions: List<SettingsMasterDigitOption> = listOf(
        SettingsMasterDigitOption.Plain(selectedType = digitType),
        SettingsMasterDigitOption.Boxes(selectedType = digitType)
    )

    internal fun asSettings(): Settings = Settings(
        isSyncEnabled = isSyncEnabled,
        appLockType = appLockType,
        isHideCodesEnabled = isHideCodesEnabled,
        themeType = themeType,
        searchBarType = searchBarType,
        digitType = digitType,
        isCodeChangeAnimationEnabled = isCodeChangeAnimationEnabled,
        isPassBannerDismissed = isPassBannerDismissed,
        installationTime = installationTime
    )

}
