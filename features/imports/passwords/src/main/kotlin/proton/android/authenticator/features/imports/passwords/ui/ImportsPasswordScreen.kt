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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.imports.passwords.R
import proton.android.authenticator.features.imports.passwords.presentation.ImportsPasswordEvent
import proton.android.authenticator.features.imports.passwords.presentation.ImportsPasswordViewModel
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.screens.TextInputDialogScreen
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun ImportsPasswordScreen(onCompleted: (Int) -> Unit, onDismissed: () -> Unit) =
    with(hiltViewModel<ImportsPasswordViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state.event) {
            when (val event = state.event) {
                ImportsPasswordEvent.Idle -> Unit
                is ImportsPasswordEvent.OnFileImported -> onCompleted(event.importedEntriesCount)
            }

            onConsumeEvent(state.event)
        }

        TextInputDialogScreen(
            title = UiText.Resource(id = R.string.imports_password_dialog_title),
            message = UiText.Resource(id = R.string.imports_password_dialog_message),
            value = state.password,
            label = UiText.Resource(id = R.string.imports_password_dialog_label),
            placeholder = UiText.Resource(id = R.string.imports_password_dialog_placeholder),
            onValueChange = ::onPasswordChange,
            confirmText = UiText.Resource(id = uiR.string.action_import),
            isConfirmEnabled = state.isValidPassword,
            onConfirmClick = { onSubmitPassword(state.password) },
            onDismissed = onDismissed
        )
    }
