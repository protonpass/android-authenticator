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

package proton.android.authenticator.features.imports.passwords.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.imports.passwords.presentation.ImportsPasswordEvent
import proton.android.authenticator.features.imports.passwords.presentation.ImportsPasswordViewModel
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun ImportsPasswordScreen(onNavigationClick: () -> Unit, onCompleted: (Int) -> Unit) {
    with(hiltViewModel<ImportsPasswordViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state.event) {
            when (val event = state.event) {
                ImportsPasswordEvent.Idle -> Unit
                is ImportsPasswordEvent.OnFileImported -> onCompleted(event.importedEntriesCount)
            }

            onConsumeEvent(state.event)
        }

        ScaffoldScreen(
            topBar = {
                SmallTopBar(
                    navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_arrow_left),
                    onNavigationClick = onNavigationClick
                )
            }
        ) { paddingValues ->
            ImportsPasswordContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(
                        start = ThemePadding.Large,
                        top = ThemePadding.MediumLarge,
                        end = ThemePadding.Large
                    ),
                state = state,
                onPasswordChange = ::onPasswordChange,
                onVisibilityChange = ::onPasswordVisibilityChange,
                onSubmitPassword = ::onSubmitPassword
            )
        }
    }
}
