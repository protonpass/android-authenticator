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

package proton.android.authenticator.shared.ui.contents.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.bars.BarComponent
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.icons.IconComponent
import proton.android.authenticator.shared.ui.domain.components.textfields.TextFieldComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

data class SearchBottomBarContent(
    private val query: String,
    private val leadingIcon: UiIcon? = null,
    private val onLeadingIconClick: () -> Unit = {},
    private val trailingIcon: UiIcon? = null,
    private val onTrailingIconClick: () -> Unit = {}
) : Content {

    @Composable
    override fun Render() {
        var isSelected = remember { mutableStateOf(false) }

        BarComponent.BottomSelectableBar(
            modifier = Modifier.fillMaxWidth(),
            isSelected = isSelected.value,
            selectedContent = TextFieldComponent.Standard(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = {}
            ),
            unselectedContent = ContainerComponent.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.DarkGray)
                    .padding(
                        horizontal = ThemePadding.MediumLarge,
                        vertical = ThemePadding.Medium
                    ),
                contents = {
                    listOf(
                        ContainerComponent.Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = ThemePadding.Small)
                                .background(color = Color.Black)
                                .padding(
                                    horizontal = ThemePadding.Medium,
                                    vertical = ThemePadding.MediumSmall
                                ),
                            contents = {
                                buildList {
                                    leadingIcon?.let { icon ->
                                        IconComponent.Actionable(
                                            modifier = Modifier.align(alignment = Alignment.CenterStart),
                                            icon = leadingIcon,
                                            onClick = onLeadingIconClick,
                                            tint = { Theme.colorScheme.textWeak }
                                        ).also(::add)
                                    }

                                    ContainerComponent.Horizontal(
                                        modifier = Modifier.align(alignment = Alignment.Center),
                                        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall),
                                        verticalAlignment = Alignment.CenterVertically,
                                        contents = {
                                            listOf(
                                                IconComponent.Descriptive(
                                                    modifier = Modifier.size(size = 15.dp),
                                                    icon = UiIcon.Resource(resId = R.drawable.ic_magnifier),
                                                    tint = { Theme.colorScheme.textWeak }
                                                ),
                                                TextComponent.Standard(
                                                    text = UiText.Dynamic("Search"),
                                                    color = { Theme.colorScheme.textWeak },
                                                    style = { Theme.typography.body1Medium }
                                                )
                                            )
                                        }
                                    ).also(::add)

                                    trailingIcon?.let { icon ->
                                        IconComponent.Actionable(
                                            modifier = Modifier.align(alignment = Alignment.CenterEnd),
                                            icon = icon,
                                            onClick = onTrailingIconClick,
                                            tint = { Theme.colorScheme.textWeak }
                                        ).also(::add)
                                    }
                                }
                            }
                        )
                    )
                }
            )
        ).Render()
    }

}
