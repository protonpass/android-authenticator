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
import proton.android.authenticator.features.settings.master.presentation.SettingsMasterSettingsModel
import proton.android.authenticator.features.settings.master.presentation.SettingsMasterState
import proton.android.authenticator.shared.ui.domain.components.containers.MultipleClicksContainer
import proton.android.authenticator.shared.ui.domain.components.rows.NavigationRow
import proton.android.authenticator.shared.ui.domain.components.rows.SelectorRow
import proton.android.authenticator.shared.ui.domain.components.rows.ToggleRow
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun SettingsContent(
    state: SettingsMasterState.Ready,
    onDismissPassBanner: (SettingsMasterSettingsModel) -> Unit,
    onBackupsClick: () -> Unit,
    onSyncChange: (SettingsMasterSettingsModel, Boolean) -> Unit,
    onAppLockTypeChange: (SettingsMasterSettingsModel, SettingsAppLockType, Context) -> Unit,
    onTapToRevealChange: (SettingsMasterSettingsModel, Boolean) -> Unit,
    onThemeTypeChange: (SettingsMasterSettingsModel, SettingsThemeType) -> Unit,
    onSearchBarTypeChange: (SettingsMasterSettingsModel, SettingsSearchBarType) -> Unit,
    onDigitTypeChange: (SettingsMasterSettingsModel, SettingsDigitType) -> Unit,
    onCodeChangeAnimationChange: (SettingsMasterSettingsModel, Boolean) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: (String) -> Unit,
    onHowToClick: (String) -> Unit,
    onFeedbackClick: (String) -> Unit,
    onDiscoverAppClick: (String, String) -> Unit,
    onVersionNameClick: () -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
    ) {
        AnimatedVisibility(visible = bannerModel.shouldShowPassBanner) {
            SettingsPassBanner(
                onDismissClick = {
                    onDismissPassBanner(settingsModel)
                },
                onActionClick = {
                    onDiscoverAppClick(bannerModel.passBannerApp.id, bannerModel.passBannerApp.url)
                }
            )
        }

        SettingsSection(
            title = stringResource(id = R.string.settings_security_section),
            contents = listOf(
                {
                    NavigationRow(
                        titleText = UiText.Resource(id = R.string.settings_security_title_backups),
                        showNavigationIcon = true,
                        onClick = onBackupsClick
                    )
                },
                {
                    ToggleRow(
                        titleText = UiText.Resource(id = R.string.settings_security_title_sync),
                        isChecked = settingsModel.isSyncEnabled,
                        onCheckedChange = { isSyncEnabled ->
                            onSyncChange(settingsModel, isSyncEnabled)
                        }
                    )
                },
                {
                    SelectorRow(
                        titleText = UiText.Resource(id = R.string.settings_security_title_lock),
                        options = settingsModel.appLockOptions,
                        onSelectedOptionChange = { newAppLockType ->
                            onAppLockTypeChange(settingsModel, newAppLockType, context)
                        }
                    )
                },
                {
                    ToggleRow(
                        titleText = UiText.Resource(id = R.string.settings_security_title_hide_codes),
                        isChecked = settingsModel.isHideCodesEnabled,
                        onCheckedChange = { isCodeHidden ->
                            onTapToRevealChange(settingsModel, isCodeHidden)
                        }
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_appearance_section),
            contents = listOf(
                {
                    SelectorRow(
                        titleText = UiText.Resource(id = R.string.settings_appearance_title_theme),
                        options = settingsModel.themeOptions,
                        onSelectedOptionChange = { themeType ->
                            onThemeTypeChange(settingsModel, themeType)
                        }
                    )
                },
                {
                    SelectorRow(
                        titleText = UiText.Resource(id = R.string.settings_appearance_title_search_bar_position),
                        options = settingsModel.searchBarOptions,
                        onSelectedOptionChange = { searchBarType ->
                            onSearchBarTypeChange(settingsModel, searchBarType)
                        }
                    )
                },
                {
                    SelectorRow(
                        titleText = UiText.Resource(id = R.string.settings_appearance_title_digit_style),
                        options = settingsModel.digitOptions,
                        onSelectedOptionChange = { digitType ->
                            onDigitTypeChange(settingsModel, digitType)
                        }
                    )
                },
                {
                    ToggleRow(
                        titleText = UiText.Resource(id = R.string.settings_appearance_title_animate_code_change),
                        isChecked = settingsModel.isCodeChangeAnimationEnabled,
                        onCheckedChange = { isCodeChangeAnimated ->
                            onCodeChangeAnimationChange(settingsModel, isCodeChangeAnimated)
                        }
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_data_management_section),
            contents = listOf(
                {
                    NavigationRow(
                        titleText = UiText.Resource(id = R.string.settings_data_management_title_import),
                        onClick = onImportClick
                    )
                },
                {
                    NavigationRow(
                        titleText = UiText.Resource(id = R.string.settings_data_management_title_export),
                        onClick = { onExportClick(exportFileName) }
                    )
                }
            )
        )

        SettingsSection(
            title = stringResource(id = R.string.settings_support_section),
            contents = listOf(
                {
                    NavigationRow(
                        titleText = UiText.Resource(id = R.string.settings_support_title_how_to),
                        onClick = { onHowToClick(howToUrl) }
                    )
                },
                {
                    NavigationRow(
                        titleText = UiText.Resource(id = R.string.settings_support_title_feedback),
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
                        NavigationRow(
                            titleText = discoverApp.title,
                            leadingIcon = discoverApp.icon,
                            description = discoverApp.description,
                            onClick = { onDiscoverAppClick(discoverApp.id, discoverApp.url) }
                        )
                    }
                }
            )
        }

        MultipleClicksContainer(onClick = onVersionNameClick) {
            SettingsVersionRow(versionName = versionName)
        }
    }
}
