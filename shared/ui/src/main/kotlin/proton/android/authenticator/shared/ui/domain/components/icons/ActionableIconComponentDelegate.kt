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

package proton.android.authenticator.shared.ui.domain.components.icons

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText

internal class ActionableIconComponentDelegate(
    override val renderId: String,
    private val modifier: Modifier,
    private val icon: UiIcon,
    private val onClick: () -> Unit,
    private val contentDescription: UiText? = null,
    private val tint: (@Composable () -> Color)? = null
) : IconComponent {

    @Composable
    override fun Render() {
        Icon(
            modifier = modifier
                .testTag(tag = renderId)
                .clickable(onClick = onClick),
            painter = icon.asPainter(),
            contentDescription = contentDescription?.asString(),
            tint = tint?.invoke() ?: LocalContentColor.current
        )
    }

}
