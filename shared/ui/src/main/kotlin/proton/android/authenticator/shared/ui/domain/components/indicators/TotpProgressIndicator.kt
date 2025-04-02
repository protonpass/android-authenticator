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

package proton.android.authenticator.shared.ui.domain.components.indicators

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow

private const val PROGRESS_ANIMATION_LABEL = "TOTP Progress Animation"
private const val PROGRESS_ANIMATION_DURATION_MILLIS = 1_000

private const val PROGRESS_COLOR_ANIMATION_LABEL = "TOTP Progress Color Animation"
private const val PROGRESS_COLOR_ANIMATION_DURATION_MILLIS = 500

private const val PROGRESS_LIMIT_1 = 0f
private const val PROGRESS_LIMIT_2 = 0.2f
private const val PROGRESS_LIMIT_3 = 0.4f

@Composable
fun TotpProgressIndicator(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
    showShadowInCounter: Boolean
) {
    val currentProgress = remainingSeconds.toFloat().minus(1f) / totalSeconds.toFloat()

    val animatedProgress by animateFloatAsState(
        label = PROGRESS_ANIMATION_LABEL,
        targetValue = currentProgress,
        animationSpec = tween(
            durationMillis = PROGRESS_ANIMATION_DURATION_MILLIS,
            easing = LinearEasing
        )
    )

    val animatedProgressColor by animateColorAsState(
        label = PROGRESS_COLOR_ANIMATION_LABEL,
        targetValue = when (currentProgress) {
            in PROGRESS_LIMIT_2..PROGRESS_LIMIT_3 -> Theme.colorScheme.signalWarning
            in PROGRESS_LIMIT_1..PROGRESS_LIMIT_2 -> Theme.colorScheme.signalDanger
            else -> Theme.colorScheme.inputBorderFocused
        },
        animationSpec = tween(
            durationMillis = PROGRESS_COLOR_ANIMATION_DURATION_MILLIS,
            easing = LinearEasing
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size = 36.dp),
            color = animatedProgressColor,
            trackColor = Theme.colorScheme.inputBorderFocused.copy(alpha = 0.2f),
            progress = { animatedProgress },
            gapSize = 0.dp
        )

        Text(
            text = remainingSeconds.toString(),
            color = Theme.colorScheme.textNorm,
            style = if (showShadowInCounter) {
                Theme.typography.compactMedium.copy(shadow = ThemeShadow.TextDefault)
            } else {
                Theme.typography.compactMedium
            }
        )
    }
}
