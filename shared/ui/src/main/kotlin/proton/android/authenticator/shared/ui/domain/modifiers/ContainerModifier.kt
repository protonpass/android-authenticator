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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Stable
fun Modifier.containerBanner() = composed {
    val shape = remember { RoundedCornerShape(size = ThemeRadius.Medium) }
    var y by remember { mutableFloatStateOf(0f) }

    onGloballyPositioned { coordinates ->
        val positionInRoot = coordinates.positionInRoot()
        y = positionInRoot.y + coordinates.size.height
    }
        .clip(shape = shape)
        .border(
            width = ThemeThickness.Small,
            color = Theme.colorScheme.whiteAlpha12,
            shape = shape
        )
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    Theme.colorScheme.gradientBannerColor1,
                    Theme.colorScheme.gradientBannerColor2,
                    Theme.colorScheme.gradientBannerColor3,
                    Theme.colorScheme.gradientBannerColor4,
                    Theme.colorScheme.gradientBannerColor5,
                    Theme.colorScheme.gradientBannerColor6,
                    Theme.colorScheme.gradientBannerColor7,
                    Theme.colorScheme.gradientBannerColor8,
                    Theme.colorScheme.gradientBannerColor9,
                    Theme.colorScheme.gradientBannerColor10
                ),
                center = Offset(
                    x = 0f,
                    y = y
                ),
                radius = 1_600f
            )
        )
        .background(color = Theme.colorScheme.black.copy(alpha = 0.5f))
}

@Stable
internal fun Modifier.containerShadow() = composed {
    val shape = remember { RoundedCornerShape(size = ThemeRadius.Small) }

    dropShadow(shape = shape)
        .clip(shape = shape)
        .border(
            width = ThemeThickness.None,
            color = Theme.colorScheme.black.copy(alpha = 0.75f),
            shape = shape
        )
        .background(color = Theme.colorScheme.black.copy(alpha = 0.2f))
        .innerShadowDouble(
            shape = shape,
            offsetX = 2.dp,
            offsetY = 2.dp
        )
}
