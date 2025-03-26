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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Stable
fun Modifier.backgroundPrimaryButton() = composed {
    dropShadow(
        shape = CircleShape,
        color = Theme.colorScheme.purpleAlpha25,
        blur = 24.dp
    )
        .clip(shape = CircleShape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Theme.colorScheme.buttonGradientTop,
                    Theme.colorScheme.buttonGradientBottom
                )
            )
        )
        .border(
            shape = CircleShape,
            width = ThemeThickness.Small,
            color = Theme.colorScheme.whiteAlpha12
        )
        .innerShadow(
            shape = CircleShape,
            color = Theme.colorScheme.whiteAlpha25
        )
}

@Stable
fun Modifier.backgroundSecondaryButton() = composed {
    clip(shape = CircleShape)
        .background(color = Color.Transparent)
        .border(
            shape = CircleShape,
            width = ThemeThickness.Small,
            color = Theme.colorScheme.backgroundButtonBorderWeak
        )
}

@Stable
fun Modifier.backgroundScreenGradient() = composed {
    val image = ImageBitmap.imageResource(R.drawable.bg_texture)
    val paintAlpha = remember { 40 }

    background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Theme.colorScheme.backgroundGradientTop,
                Theme.colorScheme.backgroundGradientBottom
            )
        )
    ).drawBehind {
        Paint().asFrameworkPaint()
            .apply {
                alpha = paintAlpha
                isAntiAlias = true
                shader = ImageShader(
                    image = image,
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated
                )
            }
            .also { paint ->
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawPaint(paint)
                }

                paint.reset()
            }
    }
}

@Stable
fun Modifier.backgroundTopBarGradient() = composed {
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
