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

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Composable
fun ProviderIcon(
    icon: UiIcon,
    size: Dp,
    modifier: Modifier = Modifier,
    borderRadius: Dp = ThemeRadius.MediumSmall,
    alpha: Float = 1f
) {
    Image(
        modifier = modifier
            .size(size = size)
            .clip(shape = RoundedCornerShape(size = borderRadius))
            .border(
                shape = RoundedCornerShape(size = borderRadius),
                width = ThemeThickness.Small,
                color = Theme.colorScheme.blackAlpha10
            ),
        painter = icon.asPainter(),
        contentDescription = null,
        alpha = alpha
    )
}
