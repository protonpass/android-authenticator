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

package proton.android.authenticator.features.disable.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.disable.presentation.SyncDisableEvent
import proton.android.authenticator.features.disable.presentation.SyncDisableViewModel
import proton.android.authenticator.features.sync.disable.R
import proton.android.authenticator.features.sync.shared.presentation.SyncErrorType
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.screens.AlertDialogScreen
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun SyncDisableScreen(
    onDisableError: (SyncErrorType) -> Unit,
    onDisableSuccess: () -> Unit,
    onDismissed: () -> Unit
) = with(hiltViewModel<SyncDisableViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.event) {
        when (state.event) {
            SyncDisableEvent.Idle -> Unit
            SyncDisableEvent.DisableSyncFailed -> {
                onDisableError(SyncErrorType.DisableSync)
            }

            SyncDisableEvent.DisableSyncSucceeded -> {
                onDisableSuccess()
            }
        }

        onConsumeEvent(event = state.event)
    }

    AlertDialogScreen(
        title = UiText.Resource(id = R.string.sync_disable_dialog_title),
        text = UiText.Resource(id = R.string.sync_disable_dialog_message),
        confirmText = UiText.Resource(id = uiR.string.action_cancel),
        cancelText = UiText.Resource(id = R.string.sync_disable_dialog_stop_button),
        onDismissRequest = onDismissed,
        onConfirmation = onDismissed,
        onCancellation = ::onDisableSync
    )
}
