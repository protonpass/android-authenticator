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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import proton.android.authenticator.shared.ui.domain.components.buttons.DialogActionTextButton
import proton.android.authenticator.shared.ui.domain.components.textfields.FormTextField
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@[Composable OptIn(ExperimentalMaterial3Api::class)]
fun TextInputDialogScreen(
    title: UiText,
    message: UiText,
    value: String,
    label: UiText,
    placeholder: UiText,
    confirmText: UiText,
    onValueChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    isConfirmEnabled: Boolean = true
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissed
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = ThemePadding.Large))
                .background(color = Theme.colorScheme.surfaceContainerHigh)
                .padding(
                    horizontal = ThemePadding.MediumLarge,
                    vertical = ThemePadding.MediumLarge
                ),
            verticalArrangement = Arrangement.spacedBy(space = ThemePadding.Small)
        ) {
            Text(
                text = title.asString(),
                color = Theme.colorScheme.surface,
                style = Theme.typography.headline
            )

            Text(
                text = message.asString(),
                color = Theme.colorScheme.surfaceVariant,
                style = Theme.typography.body2Regular
            )

            FormTextField(
                value = value,
                label = label.asString(),
                placeholder = placeholder.asString(),
                onValueChange = onValueChange,
                isRequired = true
            )

            DialogActionTextButton(
                modifier = Modifier.align(alignment = Alignment.End),
                text = confirmText,
                isEnabled = isConfirmEnabled,
                onClick = onConfirmClick
            )
        }
    }
}
