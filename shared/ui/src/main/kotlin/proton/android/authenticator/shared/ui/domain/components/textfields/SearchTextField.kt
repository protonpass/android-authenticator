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

import android.graphics.PorterDuff
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.modifiers.innerShadow
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    initialValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true
) {
    val shape = remember { CircleShape }

    var value by remember(key1 = initialValue) {
        mutableStateOf(initialValue)
    }

    BasicTextField(
        modifier = modifier
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = 50.dp
            )
            .heightIn(min = 50.dp, max = 50.dp)
            .clip(shape = shape)
            .innerShadow(
                shape = shape,
                color = Theme.colorScheme.blackAlpha10,
                offsetX = 0.dp,
                offsetY = 2.dp,
                blur = 4.dp
            )
            .innerShadow(
                shape = shape,
                color = Theme.colorScheme.blackAlpha8,
                offsetX = 1.dp,
                offsetY = 2.dp,
                blur = 4.dp,
                blendMode = PorterDuff.Mode.OVERLAY
            )
            .innerShadow(
                shape = shape,
                color = Theme.colorScheme.whiteAlpha25,
                offsetX = 0.dp,
                offsetY = (-1).dp,
                blur = 1.dp
            )
            .innerShadow(
                shape = shape,
                color = Theme.colorScheme.whiteAlpha30,
                offsetX = 0.dp,
                offsetY = (-1).dp,
                blur = 1.dp,
                blendMode = PorterDuff.Mode.OVERLAY
            ),
        value = value,
        onValueChange = { newValue ->
            value = newValue

            onValueChange(newValue)
        },
        cursorBrush = SolidColor(value = Theme.colorScheme.accent),
        textStyle = Theme.typography.body1Regular.copy(color = Theme.colorScheme.textNorm),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.action_search),
                        color = Theme.colorScheme.textWeak
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(size = 18.dp),
                        painter = painterResource(id = R.drawable.ic_magnifier),
                        contentDescription = null,
                        tint = Theme.colorScheme.textWeak
                    )
                },
                shape = shape,
                singleLine = isSingleLine,
                enabled = true,
                isError = false,
                interactionSource = MutableInteractionSource(),
                contentPadding = PaddingValues(all = ThemePadding.None),
                colors = TextFieldDefaults.colors(
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
                    selectionColors = TextSelectionColors(
                        handleColor = Theme.colorScheme.accent,
                        backgroundColor = Theme.colorScheme.accent.copy(alpha = 0.4F)
                    )
                )
            )
        }
    )
}
