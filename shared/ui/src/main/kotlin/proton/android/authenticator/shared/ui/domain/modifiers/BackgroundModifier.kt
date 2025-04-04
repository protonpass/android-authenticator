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

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness
import kotlin.math.roundToInt

@Stable
fun Modifier.backgroundPrimaryButton() = composed {
    val shape = remember { CircleShape }

    dropShadow(
        shape = shape,
        color = Theme.colorScheme.purpleAlpha25,
        blur = 24.dp
    )
        .clip(shape = shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Theme.colorScheme.buttonGradientTop,
                    Theme.colorScheme.buttonGradientBottom
                )
            )
        )
        .border(
            shape = shape,
            width = ThemeThickness.Small,
            color = Theme.colorScheme.whiteAlpha12
        )
        .innerShadow(
            shape = shape,
            color = Theme.colorScheme.whiteAlpha25
        )
}

@Stable
fun Modifier.backgroundSecondaryButton() = composed {
    val shape = remember { CircleShape }

    clip(shape = shape)
        .background(color = Color.Transparent)
        .border(
            shape = shape,
            width = ThemeThickness.Small,
            color = Theme.colorScheme.backgroundButtonBorderWeak
        )
}

@Stable
fun Modifier.backgroundScreenGradient() = composed {
    val image = ImageBitmap.imageResource(R.drawable.bg_texture)
    val paintAlpha = remember { 38 }

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
fun Modifier.backgroundDropdownMenu() = composed {
    val shape = remember { RoundedCornerShape(size = ThemeRadius.MediumSmall) }

    dropShadow(
        shape = CircleShape,
        color = Theme.colorScheme.blackAlpha8,
        offsetX = 0.dp,
        offsetY = 2.dp,
        blur = 4.dp
    )
        .clip(shape = shape)
        .background(color = Theme.colorScheme.backgroundDropdown)
        .innerShadow(
            shape = shape,
            color = Theme.colorScheme.whiteAlpha20,
            offsetX = 0.dp,
            offsetY = 1.dp,
            blur = 0.dp
        )
}

@Stable
fun Modifier.backgroundActionButton() = composed {
    val shape = remember { CircleShape }

    dropShadow(
        shape = shape,
        color = Theme.colorScheme.blackAlpha10,
        offsetX = 0.dp,
        offsetY = 2.dp,
        blur = 4.dp
    )
        .clip(shape = shape)
        .border(
            shape = shape,
            width = ThemeThickness.Small,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Theme.colorScheme.actionButtonBorderGradientTop,
                    Theme.colorScheme.actionButtonBorderGradientBottom
                )
            )
        )
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Theme.colorScheme.actionButtonBackgroundGradientTop,
                    Theme.colorScheme.actionButtonBackgroundGradientBottom
                )
            )
        )
        .innerShadow(
            shape = shape,
            color = Theme.colorScheme.whiteAlpha20,
            offsetX = 0.dp,
            offsetY = 1.dp,
            blur = 1.dp
        )
}

@Stable
fun Modifier.backgroundSection(applyShadow: Boolean = false) = composed {
    val shape = remember { RoundedCornerShape(size = ThemeRadius.Medium) }
    val shadowColor = Theme.colorScheme.blackAlpha12

    applyIf(
        condition = applyShadow,
        ifTrue = {
            dropShadow(
                shape = shape,
                color = shadowColor,
                offsetX = 0.dp,
                offsetY = 2.dp,
                blur = 8.dp
            )
        }
    )
        .clip(shape = shape)
        .border(
            width = ThemeThickness.Small,
            color = Theme.colorScheme.menuListBorder,
            shape = shape
        )
        .background(color = Theme.colorScheme.menuListBackground)

}

@Suppress("MagicNumber", "LongMethod")
@Stable
fun Modifier.backgroundOnboarding() = composed {
    val gradientShader = remember {
        RadialGradientShader(
            center = Offset(
                x = -90f,
                y = 563.5f
            ),
            radius = 889.99f,
            colors = listOf(
                Color(0xFFFFBD80),
                Color(0xFFF17995),
                Color(0xFFBF52FF),
                Color(0xFF9F4CFF),
                Color(0xFF874AFF),
                Color(0xFF704CFF)
            ),
            colorStops = listOf(
                0f,
                0.29f,
                0.65f,
                0.77f,
                0.88f,
                1f
            )
        )
    }

    val paint = remember {
        Paint().apply {
            isAntiAlias = true
            shader = gradientShader
            alpha = 0.8f

            asFrameworkPaint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            }
        }
    }

    val image = ImageBitmap.imageResource(id = R.drawable.preview_authenticator_dark)

    val skewX = remember { 0f }
    val skewY = remember { 0.4f }

    drawWithContent {
        drawContent()

        drawIntoCanvas { canvas ->
            canvas.apply {
                save()

                val scaledImage = Bitmap.createScaledBitmap(
                    image.asAndroidBitmap(),
                    231.dp.toPx().roundToInt(),
                    390.dp.toPx().roundToInt(),
                    false
                )

                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height.minus(100)
                )

                skew(
                    sx = skewX,
                    sy = -skewY
                )

                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height.plus(100)
                )

                skew(
                    sx = skewX,
                    sy = skewY
                )

                drawImage(
                    image = scaledImage.asImageBitmap(),
                    topLeft = Offset(
                        x = size.width.div(2) - scaledImage.width.div(2),
                        y = 0f
                    )
                )

                restore()

                skew(
                    sx = -skewX,
                    sy = -skewY
                )

                drawRect(
                    rect = Rect(
                        left = 0f,
                        top = scaledImage.height.toFloat().plus(75),
                        right = size.width,
                        bottom = size.height.div(2)
                    ),
                    paint = paint
                )
            }
        }
    }
}
