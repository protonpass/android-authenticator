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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.settings.master.presentation.SettingsMasterViewModel
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
fun SettingsScreen(onNavigationClick: () -> Unit, onDiscoverAppClick: (String) -> Unit) =
    with(hiltViewModel<SettingsMasterViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        ScaffoldScreen(
            topBar = {
                SettingsTopBar(
                    onNavigationClick = onNavigationClick
                )
            }
        ) { paddingValues ->
            SettingsContent(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(paddingValues = paddingValues)
                    .padding(horizontal = ThemePadding.Medium),
                state = state,
                onDismissPassBanner = ::onUpdateIsPassBannerDismissed,
                onBackupChange = ::onUpdateIsBackupEnabled,
                onSyncChange = ::onUpdateIsSyncEnabled,
                onAppLockTypeChange = ::onUpdateAppLockType,
                onTapToRevealChange = ::onUpdateIsTapToRevealEnabled,
                onThemeTypeChange = ::onUpdateThemeType,
                onSearchBarTypeChange = ::onUpdateSearchBarType,
                onDigitTypeChange = ::onUpdateDigitType,
                onCodeChangeAnimationChange = ::onUpdateIsCodeChangeAnimationEnabled,
                onDiscoverAppClick = onDiscoverAppClick
            )
        }
    }
