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

package proton.android.authenticator.shared.ui.domain.components.textfields

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Composable
fun FormPlainTextField(
    value: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isRequired: Boolean = false,
    isSingleLine: Boolean = true,
    enableQuickClear: Boolean = true
) {
    TextField(
        modifier = modifier
            .border(
                shape = RoundedCornerShape(size = ThemeRadius.MediumSmall),
                width = ThemeThickness.Small,
                color = Theme.colorScheme.inputBorder
            ),
        value = value,
        onValueChange = onValueChange,
        label = {
            if (isRequired) {
                Text(text = "$label (${stringResource(id = R.string.action_required)})")
            } else {
                Text(text = label)
            }
        },
        placeholder = {
            Text(text = placeholder)
        },
        trailingIcon = {
            if (enableQuickClear && value.isNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .clickable { onValueChange("") },
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = null,
                    tint = Theme.colorScheme.textWeak
                )
            }
        },
        isError = isError,
        singleLine = isSingleLine,
        shape = RoundedCornerShape(size = ThemeRadius.MediumSmall),
        textStyle = Theme.typography.body1Regular,
        colors = TextFieldDefaults.colors(
            errorTextColor = Theme.colorScheme.signalError,
            errorLabelColor = Theme.colorScheme.signalError,
            errorContainerColor = Theme.colorScheme.inputBackground,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = Theme.colorScheme.textNorm,
            unfocusedTextColor = Theme.colorScheme.textNorm,
            focusedLabelColor = Theme.colorScheme.textNorm,
            unfocusedLabelColor = Theme.colorScheme.textNorm,
            focusedPlaceholderColor = Theme.colorScheme.textWeak,
            unfocusedPlaceholderColor = Theme.colorScheme.textWeak,
            focusedContainerColor = Theme.colorScheme.inputBackground,
            unfocusedContainerColor = Theme.colorScheme.inputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Theme.colorScheme.accent,
            selectionColors = TextSelectionColors(
                handleColor = Theme.colorScheme.accent,
                backgroundColor = Theme.colorScheme.accent.copy(alpha = 0.4F)
            )
        )
    )
}
