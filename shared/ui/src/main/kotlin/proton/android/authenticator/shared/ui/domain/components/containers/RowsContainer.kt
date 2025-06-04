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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Composable
fun RowsContainer(contents: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        contents.forEachIndexed { index, content ->
            content()

            if (index < contents.lastIndex) {
                HorizontalDivider(
                    color = Theme.colorScheme.menuListBorder
                )
            }
        }
    }
}
