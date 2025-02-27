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

package proton.android.authenticator.shared.ui.domain.components.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface IconComponent : Component {

    data class Actionable(
        private val icon: UiIcon,
        private val onClick: () -> Unit,
        private val modifier: Modifier = Modifier,
        private val contentDescription: UiText? = null,
        private val tint: (@Composable () -> Color)? = null
    ) : IconComponent by ActionableIconComponentDelegate(
        modifier = modifier,
        icon = icon,
        onClick = onClick,
        contentDescription = contentDescription,
        tint = tint
    )

    data class Descriptive(
        private val icon: UiIcon,
        private val modifier: Modifier = Modifier,
        private val contentDescription: UiText? = null,
        private val tint: (@Composable () -> Color)? = null
    ) : IconComponent by DescriptiveIconComponentDelegate(
        modifier = modifier,
        icon = icon,
        contentDescription = contentDescription,
        tint = tint
    )

}
