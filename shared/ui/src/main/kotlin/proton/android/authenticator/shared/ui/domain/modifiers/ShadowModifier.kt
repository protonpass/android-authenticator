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

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
internal fun Modifier.innerShadowDouble(
    shape: Shape,
    color: Color = Color.Black.copy(alpha = 0.4f),
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
) = composed {
    innerShadow(
        shape = shape,
        color = color,
        blur = blur,
        offsetY = offsetY,
        offsetX = offsetX,
        spread = spread
    ).innerShadow(
        shape = shape,
        color = color,
        blur = blur,
        offsetY = -offsetY,
        offsetX = -offsetX,
        spread = spread
    )
}

@Stable
internal fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black.copy(alpha = 0.4f),
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
) = drawWithContent {
    drawContent()

    drawIntoCanvas { canvas ->
        val spreadPixels = spread.toPx()
        val blurPixels = blur.toPx()

        val shadowSize = Size(
            width = size.width.plus(spreadPixels),
            height = size.height.plus(spreadPixels)
        )

        val shadowOutline = shape.createOutline(
            size = shadowSize,
            layoutDirection = layoutDirection,
            density = this
        )

        val paint = Paint().apply {
            this.color = color
        }

        canvas.apply {
            saveLayer(
                bounds = size.toRect(),
                paint = paint
            )

            drawOutline(
                outline = shadowOutline,
                paint = paint
            )
        }

        paint.apply {
            this.asFrameworkPaint().apply {
                this.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

                if (blurPixels > 0) {
                    this.maskFilter = BlurMaskFilter(blurPixels, BlurMaskFilter.Blur.NORMAL)
                }
            }

            this.color = Color.Black
        }

        canvas.apply {
            translate(
                dx = offsetX.toPx(),
                dy = offsetY.toPx()
            )

            drawOutline(
                outline = shadowOutline,
                paint = paint
            )

            restore()
        }
    }
}

@Stable
internal fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color.White.copy(alpha = 0.08f),
    blur: Dp = 1.dp,
    offsetY: Dp = 1.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp
) = drawBehind {
    val spreadPixels = spread.toPx()
    val blurPixels = blur.toPx()

    val shadowSize = Size(
        width = size.width.plus(spreadPixels),
        height = size.height.plus(spreadPixels)
    )

    val shadowOutline = shape.createOutline(
        size = shadowSize,
        layoutDirection = layoutDirection,
        density = this
    )

    val paint = Paint().apply {
        this.color = color

        if (blurPixels > 0) {
            this.asFrameworkPaint().apply {
                this.maskFilter = BlurMaskFilter(blurPixels, BlurMaskFilter.Blur.NORMAL)
            }
        }
    }

    drawIntoCanvas { canvas ->
        canvas.apply {
            save()

            translate(
                dx = offsetX.toPx(),
                dy = offsetY.toPx()
            )

            drawOutline(
                outline = shadowOutline,
                paint = paint
            )

            restore()
        }
    }
}
