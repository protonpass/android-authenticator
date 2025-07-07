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

package proton.android.authenticator.features.exports.completion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.exports.completion.R
import proton.android.authenticator.features.exports.completion.presentation.ExportsCompletionViewModel
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.screens.AlertDialogScreen
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun ExportsCompletionScreen(onDismissed: () -> Unit) = with(hiltViewModel<ExportsCompletionViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    AlertDialogScreen(
        title = UiText.Resource(id = R.string.exports_completion_dialog_title),
        message = UiText.Resource(
            id = R.string.exports_completion_dialog_message,
            pluralStringResource(
                id = uiR.plurals.unit_items,
                count = state.exportedEntriesCount,
                state.exportedEntriesCount
            )
        ),
        confirmText = UiText.Resource(id = uiR.string.action_ok),
        onDismissRequest = onDismissed,
        onConfirmation = onDismissed
    )
}
