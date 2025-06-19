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

package proton.android.authenticator.features.sync.master.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.sync.master.presentation.SyncMasterEvent
import proton.android.authenticator.features.sync.master.presentation.SyncMasterState
import proton.android.authenticator.features.sync.master.presentation.SyncMasterViewModel
import proton.android.authenticator.features.sync.shared.presentation.SyncErrorType
import proton.android.authenticator.shared.ui.domain.components.bars.ProtonBrandBottomBar
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun SyncMasterScreen(
    onNavigationClick: () -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onEnableError: (SyncErrorType) -> Unit,
    onEnableSuccess: () -> Unit
) = with(hiltViewModel<SyncMasterViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    ScaffoldScreen(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        topBar = {
            SmallTopBar(
                navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_arrow_left),
                onNavigationClick = onNavigationClick
            )
        },
        bottomBar = {
            ProtonBrandBottomBar()
        }
    ) { paddingValues ->
        when (val currentState = state) {
            SyncMasterState.Loading -> Unit
            is SyncMasterState.Ready -> {
                LaunchedEffect(key1 = currentState.event) {
                    when (currentState.event) {
                        SyncMasterEvent.Idle -> Unit
                        SyncMasterEvent.OnSyncEnableFailed -> {
                            onEnableError(SyncErrorType.EnableSync)
                        }

                        SyncMasterEvent.OnSyncEnableSucceeded -> {
                            onEnableSuccess()
                        }

                        SyncMasterEvent.OnUserAuthenticated -> {
                            onEnableSync(currentState.settings)
                        }
                    }

                    onConsumeEvent(event = currentState.event)
                }

                SyncMasterContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                        .padding(horizontal = ThemePadding.Large),
                    state = currentState,
                    onSignInClick = onSignIn,
                    onSignUpClick = onSignUp
                )
            }
        }
    }
}
