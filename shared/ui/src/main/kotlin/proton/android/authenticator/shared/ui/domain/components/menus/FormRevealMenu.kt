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

package proton.android.authenticator.shared.ui.domain.components.menus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundDropdownMenu
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
fun FormRevealMenu(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }

    if (isRevealed) {
        content()
    } else {
        Row(
            modifier = modifier
                .backgroundDropdownMenu()
                .clickable { isRevealed = true }
                .padding(
                    horizontal = ThemePadding.Medium,
                    vertical = ThemePadding.MediumSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(weight = 1F, fill = true),
                text = title,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.body1Regular
            )

            Icon(
                modifier = Modifier.size(size = 16.dp),
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null,
                tint = Theme.colorScheme.textNorm
            )
        }
    }
}
