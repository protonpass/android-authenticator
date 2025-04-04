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

package proton.android.authenticator.features.home.scan.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.home.scan.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import kotlin.math.roundToInt

@Composable
internal fun HomeScanCameraQrMask(cutoutRect: Rect, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            Path().apply {
                fillType = PathFillType.EvenOdd

                Rect(
                    offset = Offset.Zero,
                    size = Size(
                        width = size.width,
                        height = size.height
                    )
                ).also(::addRect)

                val roundRectPaddingPx = ThemePadding.ExtraSmall.toPx()
                val roundRectOffsetPaddingPx = roundRectPaddingPx.times(2)
                val roundRectSizePaddingPx = roundRectPaddingPx.times(4)

                RoundRect(
                    rect = Rect(
                        offset = Offset(
                            x = cutoutRect.left.plus(roundRectOffsetPaddingPx),
                            y = cutoutRect.top.plus(roundRectOffsetPaddingPx)
                        ),
                        size = Size(
                            width = cutoutRect.width.minus(roundRectSizePaddingPx),
                            height = cutoutRect.height.minus(roundRectSizePaddingPx)
                        )
                    ),
                    cornerRadius = CornerRadius(x = ThemeRadius.ExtraSmall.toPx())
                ).also(::addRoundRect)
            }.also { path ->
                drawPath(
                    path = path,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }

            Path().apply {
                val roundRectRadiusPx = ThemeRadius.Small.toPx()

                RoundRect(
                    rect = Rect(
                        offset = Offset(
                            x = cutoutRect.left.toFloat(),
                            y = cutoutRect.top.toFloat()
                        ),
                        size = Size(
                            width = cutoutRect.width.toFloat(),
                            height = cutoutRect.height.toFloat()
                        )
                    ),
                    cornerRadius = CornerRadius(
                        x = roundRectRadiusPx,
                        y = roundRectRadiusPx
                    )
                ).also(::addRoundRect)
            }.also { path ->
                val lineInterval = cutoutRect.width.div(2)
                val gapInterval = cutoutRect.width.div(2) - ThemeRadius.ExtraSmall.toPx()
                val phase = cutoutRect.width.div(4) + ThemeRadius.ExtraSmall.toPx().times(1.5f)

                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(
                        width = 6.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(lineInterval, gapInterval),
                            phase = phase
                        )
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = 0,
                        y = cutoutRect.bottom.roundToInt()
                    )
                }
                .padding(top = ThemePadding.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.width(width = 160.dp),
                text = stringResource(id = R.string.home_scan_qr_code_hint),
                color = Color.White,
                style = Theme.typography.bodyRegular,
                textAlign = TextAlign.Center
            )
        }
    }
}
