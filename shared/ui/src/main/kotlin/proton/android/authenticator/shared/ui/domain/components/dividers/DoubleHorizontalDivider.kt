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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Composable
fun DoubleHorizontalDivider(
    modifier: Modifier = Modifier,
    topColor: Color = Color.Black.copy(alpha = 0.2f),
    topThickness: Dp = ThemeThickness.Small,
    bottomColor: Color = Color.White.copy(alpha = 0.12f),
    bottomThickness: Dp = ThemeThickness.Small
) {
    Column(
        modifier = modifier
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(top = ThemePadding.Small),
            thickness = topThickness,
            color = topColor
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = ThemePadding.Small),
            thickness = bottomThickness,
            color = bottomColor
        )
    }
}
