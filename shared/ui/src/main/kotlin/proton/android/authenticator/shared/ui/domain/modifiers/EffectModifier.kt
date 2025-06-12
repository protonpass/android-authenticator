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

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import proton.android.authenticator.shared.ui.domain.theme.Theme

private const val SHIMMER_EFFECT_ANIMATION_DURATION_MILLIS = 1_000

fun Modifier.shimmerEffect(animationDurationMillis: Int = SHIMMER_EFFECT_ANIMATION_DURATION_MILLIS): Modifier =
    composed {
        var size by remember { mutableStateOf(value = IntSize.Zero) }

        val transition = rememberInfiniteTransition()

        val startOffsetX by transition.animateFloat(
            initialValue = size.width.toFloat().times(-2),
            targetValue = size.width.toFloat().times(2),
            animationSpec = infiniteRepeatable(
                animation = tween(animationDurationMillis)
            )
        )

        background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Theme.colorScheme.blackAlpha20,
                    Theme.colorScheme.blackAlpha12,
                    Theme.colorScheme.blackAlpha20
                ),
                start = Offset(
                    x = startOffsetX,
                    y = 0f
                ),
                end = Offset(
                    x = startOffsetX.plus(size.width.toFloat()),
                    y = size.height.toFloat()
                )
            )
        ).onGloballyPositioned { layoutCoordinates ->
            size = layoutCoordinates.size
        }
    }
