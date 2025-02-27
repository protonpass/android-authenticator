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

package proton.android.authenticator.shared.ui.domain.modifiers

import androidx.compose.foundation.background
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Stable
internal fun Modifier.backgroundScreenGradient() = composed {
    background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Theme.colorScheme.gradientBackgroundColor1,
                Theme.colorScheme.gradientBackgroundColor2,
                Theme.colorScheme.gradientBackgroundColor3,
                Theme.colorScheme.gradientBackgroundColor4
            )
        )
    )
}

@Stable
internal fun Modifier.backgroundTopBarGradient() = composed {
    var x by remember { mutableFloatStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    onGloballyPositioned { coordinates ->
        size = coordinates.size
        val positionInRoot = coordinates.positionInRoot()
        x = positionInRoot.x + size.width / 2f
    }.background(
        brush = Brush.radialGradient(
            colors = listOf(
                Theme.colorScheme.gradientTopBarColor1,
                Theme.colorScheme.gradientTopBarColor2
            ),
            center = Offset(x = x, y = -350f),
            radius = 600f
        )
    )
}
