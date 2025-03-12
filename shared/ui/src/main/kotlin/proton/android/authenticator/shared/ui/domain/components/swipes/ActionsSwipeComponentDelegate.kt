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

package proton.android.authenticator.shared.ui.domain.components.swipes

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import kotlin.math.roundToInt

internal class ActionsSwipeComponentDelegate(
    override val renderId: String,
    private val modifier: Modifier,
    private val content: Component,
    private val actions: RowScope.() -> List<Component>,
    private val onExpanded: () -> Unit,
    private val onCollapsed: () -> Unit,
    private val isRevealed: Boolean
) : SwipeComponent {

    @Composable
    override fun Render() {
        var actionsMenuWidth by remember {
            mutableFloatStateOf(0f)
        }

        val offset = remember {
            Animatable(initialValue = 0f)
        }

        val scope = rememberCoroutineScope()

        LaunchedEffect(isRevealed, actionsMenuWidth) {
            val targetValue = if (isRevealed) {
                -actionsMenuWidth
            } else {
                0f
            }
            offset.animateTo(targetValue = targetValue)
        }

        ContainerComponent.Box(
            renderId = renderId,
            modifier = modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            contents = {
                listOf(
                    ContainerComponent.Horizontal(
                        renderId = "$renderId-actions",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterEnd)
                            .onSizeChanged { size ->
                                actionsMenuWidth = size.width.toFloat()
                            },
                        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                        verticalAlignment = Alignment.CenterVertically,
                        contents = actions
                    ),
                    ContainerComponent.Surface(
                        renderId = "$renderId-content",
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(offset.value.roundToInt(), 0) }
                            .pointerInput(actionsMenuWidth) {
                                detectHorizontalDragGestures(
                                    onDragStart = { },
                                    onDragEnd = {
                                        if (offset.value <= -actionsMenuWidth / 2f) {
                                            scope.launch {
                                                offset.animateTo(-actionsMenuWidth)
                                                onExpanded()
                                            }
                                        } else {
                                            scope.launch {
                                                offset.animateTo(0f)
                                                onCollapsed()
                                            }
                                        }
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        scope.launch {
                                            val newOffset = offset.value + dragAmount
                                            offset.snapTo(
                                                newOffset.coerceIn(
                                                    minimumValue = -actionsMenuWidth,
                                                    maximumValue = 0f
                                                )
                                            )
                                        }
                                    }
                                )
                            },
                        content = content
                    )
                )
            }
        ).Render()
    }

}
