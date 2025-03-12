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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.renders.Renderable

internal sealed interface ContainerComponent : Component {

    data class Box(
        private val contents: BoxScope.() -> List<Renderable>,
        private val modifier: Modifier = Modifier,
        private val contentAlignment: Alignment = Alignment.TopStart
    ) : ContainerComponent by BoxContainerComponentDelegate(
        modifier = modifier,
        contentAlignment = contentAlignment,
        contents = contents
    )

    data class Horizontal(
        private val contents: RowScope.() -> List<Renderable>,
        private val modifier: Modifier = Modifier,
        private val horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        private val verticalAlignment: Alignment.Vertical = Alignment.Top
    ) : ContainerComponent by HorizontalContainerComponentDelegate(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        contents = contents
    )

    data class Surface(
        private val content: Renderable,
        private val modifier: Modifier = Modifier
    ) : ContainerComponent by SurfaceContainerComponentDelegate(
        modifier = modifier,
        content = content
    )

    data class Vertical(
        private val contents: ColumnScope.() -> List<Renderable>,
        private val modifier: Modifier = Modifier,
        private val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        private val horizontalAlignment: Alignment.Horizontal = Alignment.Start
    ) : ContainerComponent by VerticalContainerComponentDelegate(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        contents = contents
    )

    data class VerticalList(
        private val contents: LazyListScope.() -> List<Renderable>,
        private val modifier: Modifier = Modifier,
        private val reverseLayout: Boolean = false,
        private val verticalArrangement: Arrangement.Vertical = if (reverseLayout) {
            Arrangement.Bottom
        } else {
            Arrangement.Top
        }
    ) : ContainerComponent by VerticalListContainerComponentDelegate(
        modifier = modifier,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        contents = contents
    )

}
