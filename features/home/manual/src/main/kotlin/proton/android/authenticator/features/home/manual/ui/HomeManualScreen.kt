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

package proton.android.authenticator.features.home.manual.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.home.manual.presentation.HomeManualEvent
import proton.android.authenticator.features.home.manual.presentation.HomeManualState
import proton.android.authenticator.features.home.manual.presentation.HomeManualViewModel
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun HomeManualScreen(
    snackbarHostState: SnackbarHostState,
    onNavigationClick: () -> Unit,
    onEntryCreated: () -> Unit,
    onEntryUpdated: () -> Unit
) = with(hiltViewModel<HomeManualViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.event) {
        when (state.event) {
            HomeManualEvent.Idle -> Unit
            HomeManualEvent.OnEntryCreated -> onEntryCreated()
            HomeManualEvent.OnEntryUpdated -> onEntryUpdated()
        }

        onEventConsumed(state.event)
    }

    when (val currentState = state) {
        HomeManualState.Loading -> Unit
        is HomeManualState.Ready -> {
            ScaffoldScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundScreenGradient(),
                snackbarHostState = snackbarHostState,
                topBar = {
                    SmallTopBar(
                        title = currentState.title,
                        navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_cross),
                        onNavigationClick = onNavigationClick,
                        action = UiText.Resource(id = uiR.string.action_save),
                        isActionEnabled = currentState.formModel.isValid,
                        onActionClick = { onSubmitForm(currentState.formModel) }
                    )
                }
            ) { paddingValues ->
                HomeManualContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState())
                        .padding(paddingValues = paddingValues)
                        .padding(horizontal = ThemePadding.Medium),
                    formModel = currentState.formModel,
                    onTitleChange = ::onTitleChange,
                    onSecretChange = ::onSecretChange,
                    onIssuerChange = ::onIssuerChange,
                    onDigitsChange = ::onDigitsChange,
                    onTimeIntervalChange = ::onTimeIntervalChange,
                    onAlgorithmChange = ::onAlgorithmChange,
                    onTypeChange = ::onTypeChange,
                    onShowAdvanceOptions = ::onShowAdvanceOptions
                )
            }
        }
    }
}
