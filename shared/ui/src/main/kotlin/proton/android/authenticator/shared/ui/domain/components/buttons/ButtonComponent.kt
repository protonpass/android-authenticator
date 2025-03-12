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

package proton.android.authenticator.shared.ui.domain.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface ButtonComponent : Component {

    data class Icon(
        override val renderId: String,
        private val icon: UiIcon,
        private val onClick: () -> Unit,
        private val modifier: Modifier = Modifier,
        private val tint: (@Composable () -> Color)? = null
    ) : ButtonComponent by IconButtonComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        onClick = onClick,
        icon = icon,
        tint = tint
    )

    data class Text(
        override val renderId: String,
        private val text: UiText,
        private val onClick: () -> Unit,
        private val modifier: Modifier = Modifier,
        private val color: (@Composable () -> Color),
        private val textColor: (@Composable () -> Color),
        private val textStyle: (@Composable () -> TextStyle)
    ) : ButtonComponent by TextButtonComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        onClick = onClick,
        text = text,
        color = color,
        textColor = textColor,
        textStyle = textStyle
    )

}
