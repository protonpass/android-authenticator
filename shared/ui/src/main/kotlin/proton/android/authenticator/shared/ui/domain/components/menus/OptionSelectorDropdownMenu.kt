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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiSelectorOption
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
fun <T> OptionSelectorDropdownMenu(
    isExpanded: Boolean,
    options: List<UiSelectorOption<T>>,
    onSelectedOptionChange: (T) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        containerColor = Theme.colorScheme.backgroundGradientBottom
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1f, fill = true),
                            text = option.text.asString(),
                            style = Theme.typography.body2Regular
                        )

                        if (option.isSelected) {
                            Icon(
                                modifier = Modifier.size(size = ThemeSpacing.Medium),
                                painter = painterResource(id = R.drawable.ic_checkmark),
                                contentDescription = null
                            )
                        }
                    }
                },
                onClick = {
                    onSelectedOptionChange(option.value)
                    onDismissRequest()
                },
                colors = MenuDefaults.itemColors().copy(
                    textColor = Theme.colorScheme.textNorm,
                    leadingIconColor = Theme.colorScheme.textNorm
                )
            )
        }
    }
}
