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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiSelectorOption
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundDropdownMenu
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
fun <T> FormDropdownMenu(
    title: String,
    options: List<UiSelectorOption<T>>,
    onSelectedOptionChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedOption = remember(key1 = options) {
        options.first(UiSelectorOption<T>::isSelected)
    }

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .backgroundDropdownMenu()
                .clickable { isExpanded = !isExpanded }
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = ThemePadding.Small)
            ) {
                Text(
                    text = selectedOption.text.asString(),
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.body1Regular
                )

                Box {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_selector),
                        contentDescription = null,
                        tint = Theme.colorScheme.textNorm
                    )

                    OptionSelectorDropdownMenu(
                        isExpanded = isExpanded,
                        options = options,
                        onSelectedOptionChange = onSelectedOptionChange,
                        onDismissRequest = { isExpanded = false }
                    )
                }
            }
        }
    }
}
