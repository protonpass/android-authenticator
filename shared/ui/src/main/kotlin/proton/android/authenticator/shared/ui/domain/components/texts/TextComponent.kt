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

package proton.android.authenticator.shared.ui.domain.components.texts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface TextComponent : Component {

    data class Totp(
        override val renderId: String,
        private val text: UiText,
        private val color: @Composable () -> Color,
        private val style: @Composable () -> TextStyle,
        private val modifier: Modifier = Modifier
    ) : TextComponent by TotpTextComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        text = text,
        color = color,
        style = style
    )

    data class Standard(
        override val renderId: String,
        private val text: UiText,
        private val color: @Composable () -> Color,
        private val style: @Composable () -> TextStyle,
        private val modifier: Modifier = Modifier,
        private val textAlign: TextAlign? = null
    ) : TextComponent by StandardTextComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        text = text,
        color = color,
        style = style,
        textAlign = textAlign
    )

}
