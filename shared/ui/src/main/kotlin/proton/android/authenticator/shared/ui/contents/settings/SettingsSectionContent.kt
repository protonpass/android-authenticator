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

package proton.android.authenticator.shared.ui.contents.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.dividers.DividerComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.containerSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

data class SettingsSectionContent(
    private val name: UiText,
    private val settingsRows: List<SettingsRowContent>
) : Content {

    @Composable
    override fun Render() {
        ContainerComponent.Vertical(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
            contents = {
                listOf(
                    TextComponent.Standard(
                        modifier = Modifier.padding(start = ThemeSpacing.Medium),
                        text = name.apply { this.mode = UiText.Mode.Uppercase },
                        color = { Theme.colorScheme.textWeak },
                        style = { Theme.typography.header }
                    ),
                    ContainerComponent.Vertical(
                        modifier = Modifier
                            .fillMaxWidth()
                            .containerSection(),
                        contents = {
                            buildList {
                                settingsRows.forEachIndexed { index, settingsRow ->
                                    settingsRow.also(::add)

                                    if (index < settingsRows.lastIndex) {
                                        DividerComponent.SimpleHorizontal(
                                            color = Color.White.copy(alpha = 0.12f)
                                        ).also(::add)
                                    }
                                }
                            }
                        }
                    )
                )
            }
        ).Render()
    }

}
