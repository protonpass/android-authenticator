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

package proton.android.authenticator.features.settings.master.ui

import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.shared.ui.contents.banners.PromoBannerContent
import proton.android.authenticator.shared.ui.contents.bars.CenterAlignedTopBarContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSectionContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSelectorRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSimpleRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsToggleRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsVersionRowContent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.R as uiR

internal class SettingsMasterScreen(
    private val onNavigationClick: () -> Unit
) : ScaffoldScreen() {

    override val topBarContent: Content = CenterAlignedTopBarContent(
        title = UiText.Resource(resId = R.string.settings_screen_title),
        navigationIcon = UiIcon.Resource(resId = uiR.drawable.ic_arrow_left),
        onNavigationClick = onNavigationClick
    )

    override val bodyContents: List<Content> = listOf(
        PromoBannerContent(
            title = UiText.Dynamic("Proton Pass"),
            description = UiText.Dynamic("Free password manager with identity protection."),
            actionText = UiText.Dynamic("Get Proton Pass"),
            onActionClick = {},
            onDismissClick = {}
        ),
        SettingsSectionContent(
            name = UiText.Resource(resId = R.string.settings_security_section),
            settingsRows = listOf(
                SettingsToggleRowContent(
                    title = UiText.Resource(resId = R.string.settings_security_title_backup),
                    description = UiText.Resource(resId = R.string.settings_security_description_backup),
                    isChecked = true,
                    onCheckedChange = {}
                ),
                SettingsToggleRowContent(
                    title = UiText.Resource(resId = R.string.settings_security_title_sync),
                    isChecked = false,
                    onCheckedChange = {}
                ),
                SettingsSelectorRowContent(
                    title = UiText.Resource(resId = R.string.settings_security_title_lock),
                    selectedOption = UiText.Dynamic("Biometric"),
                    options = listOf(
                        UiText.Dynamic("Biometric"),
                        UiText.Dynamic("Pin"),
                        UiText.Dynamic("None")
                    )
                ),
                SettingsToggleRowContent(
                    title = UiText.Resource(resId = R.string.settings_security_title_reveal),
                    isChecked = true,
                    onCheckedChange = {}
                )
            )
        ),
        SettingsSectionContent(
            name = UiText.Resource(resId = R.string.settings_appearance_section),
            settingsRows = listOf(
                SettingsSelectorRowContent(
                    title = UiText.Resource(resId = R.string.settings_appearance_title_theme),
                    selectedOption = UiText.Dynamic("Dark"),
                    options = listOf(
                        UiText.Dynamic("Light"),
                        UiText.Dynamic("Dark"),
                        UiText.Dynamic("System default")
                    )
                ),
                SettingsSelectorRowContent(
                    title = UiText.Resource(resId = R.string.settings_appearance_title_style),
                    selectedOption = UiText.Dynamic("Expanded"),
                    options = listOf(
                        UiText.Dynamic("Compact"),
                        UiText.Dynamic("Expanded")
                    )
                )
            )
        ),
        SettingsSectionContent(
            name = UiText.Resource(resId = R.string.settings_data_management_section),
            settingsRows = listOf(
                SettingsSimpleRowContent(
                    title = UiText.Resource(resId = R.string.settings_data_management_title_import)
                ),
                SettingsSimpleRowContent(
                    title = UiText.Resource(resId = R.string.settings_data_management_title_export)
                )
            )
        ),
        SettingsSectionContent(
            name = UiText.Resource(resId = R.string.settings_support_section),
            settingsRows = listOf(
                SettingsSimpleRowContent(
                    title = UiText.Resource(resId = R.string.settings_support_title_how_to)
                ),
                SettingsSimpleRowContent(
                    title = UiText.Resource(resId = R.string.settings_support_title_feedback)
                )
            )
        ),
        SettingsSectionContent(
            name = UiText.Resource(resId = R.string.settings_discover_section),
            settingsRows = listOf(
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_pass),
                    title = UiText.Resource(resId = R.string.settings_discover_title_pass)
                ),
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_vpn),
                    title = UiText.Resource(resId = R.string.settings_discover_title_vpn)
                ),
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_mail),
                    title = UiText.Resource(resId = R.string.settings_discover_title_mail)
                ),
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_drive),
                    title = UiText.Resource(resId = R.string.settings_discover_title_drive)
                ),
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_calendar),
                    title = UiText.Resource(resId = R.string.settings_discover_title_calendar)
                ),
                SettingsSimpleRowContent(
                    icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_wallet),
                    title = UiText.Resource(resId = R.string.settings_discover_title_wallet)
                )
            )
        ),
        SettingsVersionRowContent(
            text = UiText.Resource(resId = R.string.settings_app_version)
        )
    )

    override val bottomBarContent: Content? = null

}
