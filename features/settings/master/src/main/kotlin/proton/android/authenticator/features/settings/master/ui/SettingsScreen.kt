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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun SettingsScreen(onNavigationClick: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        containerColor = Color.Transparent,
        topBar = {
            SettingsTopBar(onNavigationClick = onNavigationClick)
        },
        bottomBar = {
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues = paddingValues)
                .padding(horizontal = ThemePadding.Medium),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
        ) {
            SettingsPassBanner(
                onDismissClick = {},
                onActionClick = {}
            )

            SettingsSection(
                title = stringResource(id = R.string.settings_security_section),
                contents = listOf(
                    {
                        SettingsToggleRow(
                            title = stringResource(id = R.string.settings_security_title_backup),
                            description = stringResource(id = R.string.settings_security_description_backup),
                            isChecked = true,
                            onCheckedChange = {}
                        )
                    },
                    {
                        SettingsSelectorRow(
                            title = stringResource(id = R.string.settings_security_title_lock),
                            selectedOption = "Biometric",
                            options = listOf("Biometric", "Pin", "None")
                        )
                    },
                    {
                        SettingsToggleRow(
                            title = stringResource(id = R.string.settings_security_title_sync),
                            isChecked = false,
                            onCheckedChange = {}
                        )
                    },
                    {
                        SettingsToggleRow(
                            title = stringResource(id = R.string.settings_security_title_reveal),
                            isChecked = true,
                            onCheckedChange = {}
                        )
                    }
                )
            )

            SettingsSection(
                title = stringResource(id = R.string.settings_appearance_section),
                contents = listOf(
                    {
                        SettingsSelectorRow(
                            title = stringResource(id = R.string.settings_appearance_title_theme),
                            selectedOption = "Dark",
                            options = listOf("Light", "Dark", "System default")
                        )
                    },
                    {
                        SettingsSelectorRow(
                            title = stringResource(id = R.string.settings_appearance_title_style),
                            selectedOption = "Expanded",
                            options = listOf("Compact", "Expanded")
                        )
                    }
                )
            )

            SettingsSection(
                title = stringResource(id = R.string.settings_data_management_section),
                contents = listOf(
                    {
                        SettingsNavigationRow(
                            title = stringResource(id = R.string.settings_data_management_title_import)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            title = stringResource(id = R.string.settings_data_management_title_export)
                        )
                    }
                )
            )

            SettingsSection(
                title = stringResource(id = R.string.settings_support_section),
                contents = listOf(
                    {
                        SettingsNavigationRow(
                            title = stringResource(id = R.string.settings_support_title_how_to)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            title = stringResource(id = R.string.settings_support_title_feedback)
                        )
                    }
                )
            )

            SettingsSection(
                title = stringResource(id = R.string.settings_support_section),
                contents = listOf(
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_pass,
                            title = stringResource(id = R.string.settings_discover_title_pass)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_vpn,
                            title = stringResource(id = R.string.settings_discover_title_vpn)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_mail,
                            title = stringResource(id = R.string.settings_discover_title_mail)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_drive,
                            title = stringResource(id = R.string.settings_discover_title_drive)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_calendar,
                            title = stringResource(id = R.string.settings_discover_title_calendar)
                        )
                    },
                    {
                        SettingsNavigationRow(
                            iconResId = uiR.drawable.ic_logo_wallet,
                            title = stringResource(id = R.string.settings_discover_title_wallet)
                        )
                    }
                )
            )

            SettingsVersionRow()
        }
    }
}
