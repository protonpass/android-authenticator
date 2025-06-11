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

package proton.android.authenticator.shared.ui.domain.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Composable
fun AlertDialogScreen(
    title: UiText,
    text: UiText,
    confirmText: UiText,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: UiText? = null,
    onCancellation: (() -> Unit)? = null
) {
    AlertDialog(
        modifier = modifier,
        titleContentColor = Theme.colorScheme.surface,
        textContentColor = Theme.colorScheme.surfaceVariant,
        containerColor = Theme.colorScheme.surfaceContainerHigh,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title.asString(),
                style = Theme.typography.headline
            )
        },
        text = {
            Text(
                text = text.asString(),
                style = Theme.typography.body2Regular
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(
                    text = confirmText.asString(),
                    color = Theme.colorScheme.accent,
                    style = Theme.typography.body2Regular
                )
            }
        },
        dismissButton = {
            cancelText?.let { text ->
                TextButton(onClick = onCancellation ?: onDismissRequest) {
                    Text(
                        text = text.asString(),
                        color = Theme.colorScheme.signalError,
                        style = Theme.typography.body2Regular
                    )
                }
            }
        }
    )
}
