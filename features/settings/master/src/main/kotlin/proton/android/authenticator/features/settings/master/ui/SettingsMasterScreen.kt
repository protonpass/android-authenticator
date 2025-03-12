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
import proton.android.authenticator.shared.ui.contents.settings.SettingsListContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSectionContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSelectorRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsSimpleRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsToggleRowContent
import proton.android.authenticator.shared.ui.contents.settings.SettingsVersionRowContent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.models.UiTextMask
import proton.android.authenticator.shared.ui.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.R as uiR

internal class SettingsMasterScreen(
    private val onNavigationClick: () -> Unit
) : ScaffoldScreen() {

    override val renderId: String = SCREEN_ID

    override val topBarContent: Content = CenterAlignedTopBarContent(
        renderId = TOP_BAR_ID,
        title = UiText.Resource(resId = R.string.settings_screen_title),
        navigationIcon = UiIcon.Resource(resId = uiR.drawable.ic_arrow_left),
        onNavigationClick = onNavigationClick
    )

    override val bodyContents: List<Content> = listOf(
        SettingsListContent(
            renderId = BODY_SETTINGS_LIST_ID,
            contents = listOf(
                PromoBannerContent(
                    renderId = BODY_SETTINGS_BANNER_ID,
                    title = UiText.Dynamic("Proton Pass"),
                    description = UiText.Dynamic("Free password manager with identity protection."),
                    actionText = UiText.Dynamic("Get Proton Pass"),
                    onActionClick = {},
                    onDismissClick = {}
                ),
                SettingsSectionContent(
                    renderId = BODY_SETTINGS_SECURITY_SECTION_ID,
                    name = UiText.Resource(
                        resId = R.string.settings_security_section,
                        masks = listOf(UiTextMask.Uppercase)
                    ),
                    settingsRows = listOf(
                        SettingsToggleRowContent(
                            renderId = BODY_SETTINGS_SECURITY_BACKUP_ID,
                            title = UiText.Resource(resId = R.string.settings_security_title_backup),
                            description = UiText.Resource(resId = R.string.settings_security_description_backup),
                            isChecked = true,
                            onCheckedChange = {}
                        ),
                        SettingsToggleRowContent(
                            renderId = BODY_SETTINGS_SECURITY_SYNC_ID,
                            title = UiText.Resource(resId = R.string.settings_security_title_sync),
                            isChecked = false,
                            onCheckedChange = {}
                        ),
                        SettingsSelectorRowContent(
                            renderId = BODY_SETTINGS_SECURITY_LOCK_ID,
                            title = UiText.Resource(resId = R.string.settings_security_title_lock),
                            selectedOption = UiText.Dynamic("Biometric"),
                            options = listOf(
                                UiText.Dynamic("Biometric"),
                                UiText.Dynamic("Pin"),
                                UiText.Dynamic("None")
                            )
                        ),
                        SettingsToggleRowContent(
                            renderId = BODY_SETTINGS_SECURITY_REVEAL_ID,
                            title = UiText.Resource(resId = R.string.settings_security_title_reveal),
                            isChecked = true,
                            onCheckedChange = {}
                        )
                    )
                ),
                SettingsSectionContent(
                    renderId = BODY_SETTINGS_APPEARANCE_SECTION_ID,
                    name = UiText.Resource(
                        resId = R.string.settings_appearance_section,
                        masks = listOf(UiTextMask.Uppercase)
                    ),
                    settingsRows = listOf(
                        SettingsSelectorRowContent(
                            renderId = BODY_SETTINGS_APPEARANCE_THEME_ID,
                            title = UiText.Resource(resId = R.string.settings_appearance_title_theme),
                            selectedOption = UiText.Dynamic("Dark"),
                            options = listOf(
                                UiText.Dynamic("Light"),
                                UiText.Dynamic("Dark"),
                                UiText.Dynamic("System default")
                            )
                        ),
                        SettingsSelectorRowContent(
                            renderId = BODY_SETTINGS_APPEARANCE_STYLE_ID,
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
                    renderId = BODY_SETTINGS_DATA_SECTION_ID,
                    name = UiText.Resource(
                        resId = R.string.settings_data_management_section,
                        masks = listOf(UiTextMask.Uppercase)
                    ),
                    settingsRows = listOf(
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DATA_IMPORT_ID,
                            title = UiText.Resource(resId = R.string.settings_data_management_title_import)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DATA_EXPORT_ID,
                            title = UiText.Resource(resId = R.string.settings_data_management_title_export)
                        )
                    )
                ),
                SettingsSectionContent(
                    renderId = BODY_SETTINGS_SUPPORT_SECTION_ID,
                    name = UiText.Resource(
                        resId = R.string.settings_support_section,
                        masks = listOf(UiTextMask.Uppercase)
                    ),
                    settingsRows = listOf(
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_SUPPORT_HOW_TO_ID,
                            title = UiText.Resource(resId = R.string.settings_support_title_how_to)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_SUPPORT_FEEDBACK_ID,
                            title = UiText.Resource(resId = R.string.settings_support_title_feedback)
                        )
                    )
                ),
                SettingsSectionContent(
                    renderId = BODY_SETTINGS_DISCOVER_SECTION_ID,
                    name = UiText.Resource(
                        resId = R.string.settings_discover_section,
                        masks = listOf(UiTextMask.Uppercase)
                    ),
                    settingsRows = listOf(
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_PASS_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_pass),
                            title = UiText.Resource(resId = R.string.settings_discover_title_pass)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_VPN_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_vpn),
                            title = UiText.Resource(resId = R.string.settings_discover_title_vpn)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_MAIL_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_mail),
                            title = UiText.Resource(resId = R.string.settings_discover_title_mail)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_DRIVE_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_drive),
                            title = UiText.Resource(resId = R.string.settings_discover_title_drive)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_CALENDAR_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_calendar),
                            title = UiText.Resource(resId = R.string.settings_discover_title_calendar)
                        ),
                        SettingsSimpleRowContent(
                            renderId = BODY_SETTINGS_DISCOVER_WALLET_ID,
                            icon = UiIcon.Resource(resId = uiR.drawable.ic_logo_wallet),
                            title = UiText.Resource(resId = R.string.settings_discover_title_wallet)
                        )
                    )
                ),
                SettingsVersionRowContent(
                    renderId = BODY_SETTINGS_VERSION_ID,
                    text = UiText.Resource(resId = R.string.settings_app_version)
                )
            )
        )
    )

    override val bottomBarContent: Content? = null

    private companion object {

        private const val SCREEN_ID = "SettingsMasterScreen"

        private const val TOP_BAR_ID = "${SCREEN_ID}TopBar"

        private const val BODY_SETTINGS_LIST_ID = "${SCREEN_ID}SettingsList"

        private const val BODY_SETTINGS_BANNER_ID = "${SCREEN_ID}Banner"

        private const val BODY_SETTINGS_SECURITY_SECTION_ID = "${SCREEN_ID}SecuritySection"

        private const val BODY_SETTINGS_SECURITY_BACKUP_ID = "${SCREEN_ID}SecurityBackup"

        private const val BODY_SETTINGS_SECURITY_SYNC_ID = "${SCREEN_ID}SecuritySync"

        private const val BODY_SETTINGS_SECURITY_LOCK_ID = "${SCREEN_ID}SecurityLock"

        private const val BODY_SETTINGS_SECURITY_REVEAL_ID = "${SCREEN_ID}SecurityReveal"

        private const val BODY_SETTINGS_APPEARANCE_SECTION_ID = "${SCREEN_ID}AppearanceSection"

        private const val BODY_SETTINGS_APPEARANCE_THEME_ID = "${SCREEN_ID}AppearanceTheme"

        private const val BODY_SETTINGS_APPEARANCE_STYLE_ID = "${SCREEN_ID}AppearanceStyle"

        private const val BODY_SETTINGS_DATA_SECTION_ID = "${SCREEN_ID}DataSection"

        private const val BODY_SETTINGS_DATA_IMPORT_ID = "${SCREEN_ID}DataImport"

        private const val BODY_SETTINGS_DATA_EXPORT_ID = "${SCREEN_ID}DataExport"

        private const val BODY_SETTINGS_SUPPORT_SECTION_ID = "${SCREEN_ID}SupportSection"

        private const val BODY_SETTINGS_SUPPORT_HOW_TO_ID = "${SCREEN_ID}SupportHowTo"

        private const val BODY_SETTINGS_SUPPORT_FEEDBACK_ID = "${SCREEN_ID}SupportFeedback"

        private const val BODY_SETTINGS_DISCOVER_SECTION_ID = "${SCREEN_ID}DiscoverSection"

        private const val BODY_SETTINGS_DISCOVER_PASS_ID = "${SCREEN_ID}DiscoverPass"

        private const val BODY_SETTINGS_DISCOVER_VPN_ID = "${SCREEN_ID}DiscoverVpn"

        private const val BODY_SETTINGS_DISCOVER_MAIL_ID = "${SCREEN_ID}DiscoverMail"

        private const val BODY_SETTINGS_DISCOVER_DRIVE_ID = "${SCREEN_ID}DiscoverDrive"

        private const val BODY_SETTINGS_DISCOVER_CALENDAR_ID = "${SCREEN_ID}DiscoverCalendar"

        private const val BODY_SETTINGS_DISCOVER_WALLET_ID = "${SCREEN_ID}DiscoverWallet"

        private const val BODY_SETTINGS_VERSION_ID = "${SCREEN_ID}Version"

    }

}
