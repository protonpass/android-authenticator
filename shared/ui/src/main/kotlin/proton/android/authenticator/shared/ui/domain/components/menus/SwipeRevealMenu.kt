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

package proton.android.authenticator.shared.ui.domain.components.menus

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import kotlin.math.roundToInt

enum class SwipeDirection {
    LeftToRight, RightToLeft
}

private const val MENU_WIDTH_COEFFICIENT = 3

@Composable
fun SwipeRevealMenu(
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    gap: Dp = ThemeSpacing.None,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    leadingMenuContent: @Composable () -> Unit,
    trailingMenuContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var contentWidth by remember {
        mutableFloatStateOf(0f)
    }

    val contentOffset = remember {
        Animatable(initialValue = 0f)
    }

    val menuWith = remember(contentWidth) {
        if (contentWidth == 0f) {
            0f
        } else {
            contentWidth.div(MENU_WIDTH_COEFFICIENT)
        }
    }

    val leadingMenuOffset = remember(key1 = menuWith) {
        Animatable(initialValue = -menuWith)
    }

    val trailingMenuOffset = remember(key1 = menuWith) {
        Animatable(initialValue = menuWith)
    }

    var swipeDirection: SwipeDirection? by remember {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isRevealed, key2 = contentWidth) {
        if (isRevealed) {
            when (swipeDirection) {
                SwipeDirection.LeftToRight -> contentWidth
                SwipeDirection.RightToLeft -> -contentWidth
                null -> 0f
            }.also { menuWidth ->
                contentOffset.animateTo(menuWidth)
            }
        } else {
            contentOffset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .onSizeChanged { contentSize ->
                    contentWidth = contentSize.width.toFloat()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .offset(x = leadingMenuOffset.value.dp - gap)
            ) {
                leadingMenuContent()
            }
        }

        Box(
            modifier = Modifier
                .offset(x = trailingMenuOffset.value.dp + gap)
                .align(Alignment.CenterEnd)
        ) {
            trailingMenuContent()
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(contentOffset.value.roundToInt(), 0) }
                .pointerInput(contentWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            if (swipeDirection == null) {
                                swipeDirection = if (dragAmount > 0) {
                                    SwipeDirection.LeftToRight
                                } else {
                                    SwipeDirection.RightToLeft
                                }
                            }

                            when (swipeDirection) {
                                SwipeDirection.LeftToRight -> {
                                    Triple(
                                        first = (contentOffset.value + dragAmount)
                                            .coerceIn(0f, contentWidth),
                                        second = (leadingMenuOffset.value + dragAmount.div(MENU_WIDTH_COEFFICIENT))
                                            .coerceIn(-menuWith, 0f),
                                        third = (trailingMenuOffset.value + dragAmount.div(MENU_WIDTH_COEFFICIENT))
                                            .coerceIn(menuWith, menuWith.times(2))
                                    )
                                }

                                else -> {
                                    Triple(
                                        first = (contentOffset.value + dragAmount)
                                            .coerceIn(-contentWidth, 0f),
                                        second = (leadingMenuOffset.value + dragAmount.div(MENU_WIDTH_COEFFICIENT))
                                            .coerceIn(-menuWith.times(2), -menuWith),
                                        third = (trailingMenuOffset.value + dragAmount.div(MENU_WIDTH_COEFFICIENT))
                                            .coerceIn(0f, menuWith)
                                    )
                                }
                            }.also { (newContentOffset, newLeadingOffset, newTrailingOffset) ->
                                scope.launch {
                                    contentOffset.snapTo(newContentOffset)
                                }

                                scope.launch {
                                    leadingMenuOffset.snapTo(newLeadingOffset)
                                }

                                scope.launch {
                                    trailingMenuOffset.snapTo(newTrailingOffset)
                                }
                            }
                        },
                        onDragEnd = {
                            when (swipeDirection) {
                                SwipeDirection.LeftToRight -> {
                                    if (contentOffset.value >= contentWidth / 2f) {
                                        scope.launch {
                                            contentOffset.animateTo(contentWidth)
                                        }

                                        scope.launch {
                                            leadingMenuOffset.animateTo(0f)
                                        }

                                        scope.launch {
                                            trailingMenuOffset.animateTo(menuWith.times(2))
                                        }

                                        onExpanded()
                                    } else {
                                        scope.launch {
                                            contentOffset.animateTo(0f)
                                        }

                                        scope.launch {
                                            leadingMenuOffset.animateTo(-menuWith)
                                        }

                                        scope.launch {
                                            trailingMenuOffset.animateTo(menuWith)
                                        }

                                        onCollapsed()
                                        swipeDirection = null
                                    }
                                }

                                else -> {
                                    if (contentOffset.value <= -contentWidth / 2f) {
                                        scope.launch {
                                            contentOffset.animateTo(-contentWidth)
                                        }

                                        scope.launch {
                                            leadingMenuOffset.animateTo(-menuWith)
                                        }

                                        scope.launch {
                                            trailingMenuOffset.animateTo(0f)
                                        }

                                        onExpanded()
                                    } else {
                                        scope.launch {
                                            contentOffset.animateTo(0f)
                                        }

                                        scope.launch {
                                            leadingMenuOffset.animateTo(-menuWith)
                                        }

                                        scope.launch {
                                            trailingMenuOffset.animateTo(menuWith)
                                        }

                                        onCollapsed()
                                        swipeDirection = null
                                    }
                                }
                            }
                        }
                    )
                },
            color = Color.Transparent
        ) {
            content()
        }
    }
}
