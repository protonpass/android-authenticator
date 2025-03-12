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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.components.Component

internal sealed interface ProgressComponent : Component {

    data class Circular(
        override val renderId: String,
        private val modifier: Modifier = Modifier,
        private val color: @Composable () -> Color,
        private val trackColor: @Composable () -> Color,
        private val progress: Float
    ) : ProgressComponent by CircularProgressComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
        progress = progress
    )

    data class CircularCounter(
        override val renderId: String,
        private val current: Int,
        private val total: Int,
        private val modifier: Modifier = Modifier
    ) : ProgressComponent by CircularCounterProgressComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        current = current,
        total = total
    )

}
