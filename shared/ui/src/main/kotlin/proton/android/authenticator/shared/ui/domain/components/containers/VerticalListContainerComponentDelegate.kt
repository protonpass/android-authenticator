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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import proton.android.authenticator.shared.ui.domain.renders.Renderable

internal class VerticalListContainerComponentDelegate(
    override val renderId: String,
    private val modifier: Modifier,
    private val reverseLayout: Boolean,
    private val verticalArrangement: Arrangement.Vertical,
    private val contents: LazyListScope.() -> List<Renderable>
) : ContainerComponent {

    @Composable
    override fun Render() {
        LazyColumn(
            modifier = modifier.testTag(tag = renderId),
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement
        ) {
            items(
                items = contents(),
                key = { content -> content.renderId }
            ) { content ->
                content.Render()
            }
        }
    }

}
