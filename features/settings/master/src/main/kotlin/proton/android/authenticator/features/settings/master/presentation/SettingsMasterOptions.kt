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

import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.shared.ui.domain.models.UiSelectorOption
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface SettingsMasterAppLockOption : UiSelectorOption<SettingsAppLockType> {

    data class Biometric(override val selectedType: SettingsAppLockType) : SettingsMasterAppLockOption {

        override val isSelected: Boolean = selectedType == SettingsAppLockType.Biometric

        override val text: UiText = UiText.Resource(
            id = R.string.settings_security_title_lock_option_biometric
        )

        override val value: SettingsAppLockType = SettingsAppLockType.Biometric

    }

    data class None(override val selectedType: SettingsAppLockType) : SettingsMasterAppLockOption {

        override val isSelected: Boolean = selectedType == SettingsAppLockType.None

        override val text: UiText = UiText.Resource(
            id = R.string.settings_security_title_lock_option_none
        )

        override val value: SettingsAppLockType = SettingsAppLockType.None

    }

    data class PinCode(override val selectedType: SettingsAppLockType) : SettingsMasterAppLockOption {

        override val isSelected: Boolean = selectedType == SettingsAppLockType.PinCode

        override val text: UiText = UiText.Resource(
            id = R.string.settings_security_title_lock_option_pin_code
        )

        override val value: SettingsAppLockType = SettingsAppLockType.PinCode

    }

}

internal sealed interface SettingsMasterThemeOption : UiSelectorOption<SettingsThemeType> {

    data class Dark(override val selectedType: SettingsThemeType) : SettingsMasterThemeOption {

        override val isSelected: Boolean = selectedType == SettingsThemeType.Dark

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_theme_option_dark
        )

        override val value: SettingsThemeType = SettingsThemeType.Dark

    }

    data class Light(override val selectedType: SettingsThemeType) : SettingsMasterThemeOption {

        override val isSelected: Boolean = selectedType == SettingsThemeType.Light

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_theme_option_light
        )

        override val value: SettingsThemeType = SettingsThemeType.Light

    }

    data class System(override val selectedType: SettingsThemeType) : SettingsMasterThemeOption {

        override val isSelected: Boolean = selectedType == SettingsThemeType.System

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_theme_option_system
        )

        override val value: SettingsThemeType = SettingsThemeType.System

    }

}

internal sealed interface SettingsMasterSearchBarOption : UiSelectorOption<SettingsSearchBarType> {

    data class Bottom(override val selectedType: SettingsSearchBarType) : SettingsMasterSearchBarOption {

        override val isSelected: Boolean = selectedType == SettingsSearchBarType.Bottom

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_search_bar_position_option_bottom
        )

        override val value: SettingsSearchBarType = SettingsSearchBarType.Bottom

    }

    data class Top(override val selectedType: SettingsSearchBarType) : SettingsMasterSearchBarOption {

        override val isSelected: Boolean = selectedType == SettingsSearchBarType.Top

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_search_bar_position_option_top
        )

        override val value: SettingsSearchBarType = SettingsSearchBarType.Top

    }

}

internal sealed interface SettingsMasterDigitOption : UiSelectorOption<SettingsDigitType> {

    data class Plain(override val selectedType: SettingsDigitType) : SettingsMasterDigitOption {

        override val isSelected: Boolean = selectedType == SettingsDigitType.Plain

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_digit_style_option_plain
        )

        override val value: SettingsDigitType = SettingsDigitType.Plain

    }

    data class Rich(override val selectedType: SettingsDigitType) : SettingsMasterDigitOption {

        override val isSelected: Boolean = selectedType == SettingsDigitType.Rich

        override val text: UiText = UiText.Resource(
            id = R.string.settings_appearance_title_digit_style_option_rich
        )

        override val value: SettingsDigitType = SettingsDigitType.Rich

    }

}
