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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.buttons.DialogActionTextButton
import proton.android.authenticator.shared.ui.domain.components.indicators.DialogProgressIndicator
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
fun AlertDialogScreen(
    title: UiText,
    message: UiText,
    confirmText: UiText,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: UiText? = null,
    onCancellation: (() -> Unit)? = null,
    isLoading: Boolean = false
) {
    AlertDialogScreen(
        title = title,
        messages = message.let(::listOf),
        confirmText = confirmText,
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        modifier = modifier,
        cancelText = cancelText,
        onCancellation = onCancellation,
        isLoading = isLoading
    )
}

@Composable
fun AlertDialogScreen(
    title: UiText,
    messages: List<UiText>,
    confirmText: UiText,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: UiText? = null,
    onCancellation: (() -> Unit)? = null,
    isLoading: Boolean = false
) {
    val shouldShowActionButton = remember(key1 = isLoading) { !isLoading }

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
            Column(
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium)
            ) {
                messages.forEach { message ->
                    Text(
                        text = message.asString(),
                        style = Theme.typography.body2Regular
                    )
                }

            }
        },
        confirmButton = {
            if (shouldShowActionButton) {
                DialogActionTextButton(
                    text = confirmText,
                    onClick = onConfirmation
                )
            } else {
                DialogProgressIndicator()
            }
        },
        dismissButton = {
            if (shouldShowActionButton) {
                cancelText?.let { text ->
                    DialogActionTextButton(
                        text = text,
                        textColor = Theme.colorScheme.signalError,
                        onClick = onCancellation ?: onDismissRequest
                    )
                }
            }
        }
    )
}
