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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius

@Composable
fun FormTextField(
    initialValue: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isSingleLine: Boolean = true
) {
    var value by remember(key1 = initialValue) {
        mutableStateOf(initialValue)
    }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { newValue ->
            value = newValue

            onValueChange(newValue)
        },
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
        singleLine = isSingleLine,
        shape = RoundedCornerShape(size = ThemeRadius.MediumSmall),
        textStyle = Theme.typography.body1Regular,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Theme.colorScheme.textNorm,
            unfocusedTextColor = Theme.colorScheme.textNorm,
            focusedLabelColor = Theme.colorScheme.textNorm,
            unfocusedLabelColor = Theme.colorScheme.textNorm,
            focusedPlaceholderColor = Theme.colorScheme.textNorm.copy(alpha = 0.6F),
            focusedContainerColor = Color.Black.copy(alpha = 0.5F),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.5F),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
