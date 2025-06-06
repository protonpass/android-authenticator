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

package proton.android.authenticator.features.backups.master.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.features.backups.master.presentation.BackupsMasterViewModel
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun BackupsMasterScreen(snackbarHostState: SnackbarHostState, onNavigationClick: () -> Unit) =
    with(hiltViewModel<BackupsMasterViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        ScaffoldScreen(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient(),
            snackbarHostState = snackbarHostState,
            topBar = {
                SmallTopBar(
                    title = UiText.Resource(id = R.string.backups_screen_title),
                    navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_arrow_left),
                    onNavigationClick = onNavigationClick
                )
            }
        ) { innerPaddingValues ->
            BackupsMasterContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPaddingValues)
                    .padding(horizontal = ThemePadding.Medium),
                state = state,
                onIsEnableChange = ::onUpdateIsEnabled,
                onFrequencyChange = ::onUpdateFrequencyType,
                onBackupNowClick = ::onCreateBackup
            )
        }
    }
