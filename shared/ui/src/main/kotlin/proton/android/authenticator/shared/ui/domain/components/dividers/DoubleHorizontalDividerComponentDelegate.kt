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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent

internal class DoubleHorizontalDividerComponentDelegate(
    private val modifier: Modifier,
    private val topThickness: Dp,
    private val topColor: Color,
    private val bottomThickness: Dp,
    private val bottomColor: Color
) : DividerComponent {

    @Composable
    override fun Render() {
        ContainerComponent.Vertical(
            modifier = modifier,
            contents = {
                listOf(
                    DividerComponent.SimpleHorizontal(
                        thickness = topThickness,
                        color = topColor
                    ),
                    DividerComponent.SimpleHorizontal(
                        thickness = bottomThickness,
                        color = bottomColor
                    )
                )
            }
        ).Render()
    }

}
