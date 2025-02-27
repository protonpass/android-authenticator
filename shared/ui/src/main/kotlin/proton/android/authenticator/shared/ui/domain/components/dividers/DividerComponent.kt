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

package proton.android.authenticator.shared.ui.domain.components.dividers

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

internal interface DividerComponent : Component {

    data class SimpleHorizontal(
        private val color: Color = Color.Gray,
        private val modifier: Modifier = Modifier,
        private val thickness: Dp = ThemeThickness.Small
    ) : DividerComponent by SimpleHorizontalDividerComponentDelegate(
        modifier = modifier,
        thickness = thickness,
        color = color
    )

    data class DoubleHorizontal(
        private val modifier: Modifier = Modifier,
        private val topThickness: Dp = ThemeThickness.Small,
        private val topColor: Color = Color.Red,
        private val bottomThickness: Dp = ThemeThickness.Small,
        private val bottomColor: Color = Color.Gray
    ) : DividerComponent by DoubleHorizontalDividerComponentDelegate(
        modifier = modifier,
        topThickness = topThickness,
        topColor = topColor,
        bottomThickness = bottomThickness,
        bottomColor = bottomColor
    )

}
