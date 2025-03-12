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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.Component

internal sealed interface SwipeComponent : Component {

    data class Actions(
        override val renderId: String,
        private val content: Component,
        private val actions: RowScope.() -> List<Component>,
        private val isRevealed: Boolean,
        private val modifier: Modifier = Modifier,
        private val onExpanded: () -> Unit = {},
        private val onCollapsed: () -> Unit = {}
    ) : SwipeComponent by ActionsSwipeComponentDelegate(
        renderId = renderId,
        modifier = modifier,
        content = content,
        actions = actions,
        isRevealed = isRevealed,
        onExpanded = onExpanded,
        onCollapsed = onCollapsed
    )

}
