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

package proton.android.authenticator.shared.ui.domain.components.containers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MultipleClicksContainer(
    enabled: Boolean = true,
    animated: Boolean = false,
    clickCount: Int = 3,
    clickInterval: Long = 400L,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var currentClickCount by remember { mutableIntStateOf(0) }
    var lastClickTimestamp by remember { mutableLongStateOf(0L) }

    val clickHandler: () -> Unit = {
        if (enabled) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTimestamp <= clickInterval) {
                currentClickCount++
            } else {
                currentClickCount = 1
            }
            lastClickTimestamp = currentTime

            if (currentClickCount == clickCount) {
                currentClickCount = 0
                onClick()
            }
        }
    }

    val modifier = if (animated) {
        Modifier.clickable(onClick = clickHandler)
    } else {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = clickHandler
        )
    }

    Box(modifier = modifier) {
        content()
    }
}
