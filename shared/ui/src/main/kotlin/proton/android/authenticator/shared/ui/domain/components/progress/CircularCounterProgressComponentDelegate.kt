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

package proton.android.authenticator.shared.ui.domain.components.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme

private const val PROGRESS_ANIMATION_LABEL = "TOTP Progress Animation"
private const val PROGRESS_ANIMATION_DURATION_MILLIS = 1_000

internal class CircularCounterProgressComponentDelegate(
    private val modifier: Modifier,
    private val current: Int,
    private val total: Int
) : ProgressComponent {

    @Composable
    override fun Render() {
        val progress = remember(current, total) {
            current.toFloat() / total.toFloat()
        }

        val animatedProgress by animateFloatAsState(
            label = PROGRESS_ANIMATION_LABEL,
            targetValue = progress,
            animationSpec = tween(
                durationMillis = PROGRESS_ANIMATION_DURATION_MILLIS,
                easing = LinearEasing
            )
        )

        ContainerComponent.Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            contents = {
                listOf(
                    ProgressComponent.Circular(
                        color = { Theme.colorScheme.signalSuccess },
                        trackColor = { Theme.colorScheme.signalSuccess.copy(alpha = 0.2f) },
                        progress = animatedProgress
                    ),
                    TextComponent.Standard(
                        text = UiText.Dynamic(value = current.toString()),
                        color = { Theme.colorScheme.textNorm },
                        style = { Theme.typography.compactMedium }
                    )
                )
            }
        ).Render()
    }

}
