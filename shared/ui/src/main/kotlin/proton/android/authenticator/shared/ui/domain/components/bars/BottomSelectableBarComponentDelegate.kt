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

package proton.android.authenticator.shared.ui.domain.components.bars

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.renders.Renderable

internal class BottomSelectableBarComponentDelegate(
    override val renderId: String,
    private val selectedContent: Renderable,
    private val unselectedContent: Renderable,
    private val isSelected: Boolean,
    private val modifier: Modifier
) : BarComponent {

    @Composable
    override fun Render() {
        val content = remember(isSelected) {
            if (isSelected) selectedContent
            else unselectedContent
        }

        ContainerComponent.Box(
            renderId = renderId,
            modifier = modifier,
            contents = {
                listOf(content)
            }
        ).Render()
    }

}
