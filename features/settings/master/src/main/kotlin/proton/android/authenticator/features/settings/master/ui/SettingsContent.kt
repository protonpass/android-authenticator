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

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.features.settings.master.presentation.SettingsMasterState
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun SettingsContent(
    state: SettingsMasterState,
    onDismissPassBanner: () -> Unit,
    onBackupChange: (Boolean) -> Unit,
    onSyncChange: (Boolean) -> Unit,
    onAppLockTypeChange: (SettingsAppLockType, Context) -> Unit,
    onTapToRevealChange: (Boolean) -> Unit,
    onThemeTypeChange: (SettingsThemeType) -> Unit,
    onSearchBarTypeChange: (SettingsSearchBarType) -> Unit,
    onDigitTypeChange: (SettingsDigitType) -> Unit,
    onCodeChangeAnimationChange: (Boolean) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: (String) -> Unit,
    onHowToClick: (String) -> Unit,
    onFeedbackClick: (String) -> Unit,
    onDiscoverAppClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
    ) {
        AnimatedVisibility(visible = bannerModel.shouldShowPassBanner) {
            SettingsPassBanner(
                onDismissClick = onDismissPassBanner,
                onActionClick = {
                    onDiscoverAppClick(bannerModel.passBannerApp.id, bannerModel.passBannerApp.url)
                }
            )
        }

        SettingsSection(
            title = stringResource(id = R.string.settings_security_section),
            contents = listOf(
                {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.settings_security_title_backup),
                        description = stringResource(id = R.string.settings_security_description_backup),
                        isChecked = settingsModel.isBackupEnabled,
                        onCheckedChange = onBackupChange
                    )
                },
                {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.settings_security_title_sync),
                        isChecked = settingsModel.isSyncEnabled,
                        onCheckedChange = onSyncChange
                    )
                },
                {
                    SettingsSelectorRow<SettingsAppLockType>(
                        title = stringResource(id = R.string.settings_security_title_lock),
                        options = settingsModel.appLockOptions,
                        onSelectedOptionChange = { newAppLockType ->
                            onAppLockTypeChange(newAppLockType, context)
                        }
                    )
                },
                {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.settings_security_title_hide_codes),
                        isChecked = settingsModel.isHideCodesEnabled,
                        onCheckedChange = onTapToRevealChange
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_appearance_section),
            contents = listOf(
                {
                    SettingsSelectorRow<SettingsThemeType>(
                        title = stringResource(id = R.string.settings_appearance_title_theme),
                        options = settingsModel.themeOptions,
                        onSelectedOptionChange = onThemeTypeChange
                    )
                },
                {
                    SettingsSelectorRow<SettingsSearchBarType>(
                        title = stringResource(id = R.string.settings_appearance_title_search_bar_position),
                        options = settingsModel.searchBarOptions,
                        onSelectedOptionChange = onSearchBarTypeChange
                    )
                },
                {
                    SettingsSelectorRow<SettingsDigitType>(
                        title = stringResource(id = R.string.settings_appearance_title_digit_style),
                        options = settingsModel.digitOptions,
                        onSelectedOptionChange = onDigitTypeChange
                    )
                },
                {
                    SettingsToggleRow(
                        title = stringResource(id = R.string.settings_appearance_title_animate_code_change),
                        isChecked = settingsModel.isCodeChangeAnimationEnabled,
                        onCheckedChange = onCodeChangeAnimationChange
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_data_management_section),
            contents = listOf(
                {
                    SettingsNavigationRow(
                        title = UiText.Resource(id = R.string.settings_data_management_title_import),
                        onClick = onImportClick
                    )
                },
                {
                    SettingsNavigationRow(
                        title = UiText.Resource(id = R.string.settings_data_management_title_export),
                        onClick = { onExportClick(exportFileName) }
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_support_section),
            contents = listOf(
                {
                    SettingsNavigationRow(
                        title = UiText.Resource(id = R.string.settings_support_title_how_to),
                        onClick = { onHowToClick(howToUrl) }
                    )
                },
                {
                    SettingsNavigationRow(
                        title = UiText.Resource(id = R.string.settings_support_title_feedback),
                        onClick = { onFeedbackClick(feedbackUrl) }
                    )
                }
            )
        )

        if (discoverModel.shouldShowDiscoverSection) {
            SettingsSection(
                title = stringResource(id = R.string.settings_discover_section),
                contents = discoverModel.discoverProtonApps.map { discoverApp ->
                    {
                        SettingsNavigationRow(
                            icon = discoverApp.icon,
                            title = discoverApp.title,
                            description = discoverApp.description,
                            onClick = { onDiscoverAppClick(discoverApp.id, discoverApp.url) }
                        )
                    }
                }
            )
        }

        SettingsVersionRow()
    }
}
