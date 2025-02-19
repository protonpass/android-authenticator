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

package proton.android.authenticator.shared.ui.contents.entries

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.dividers.DividerComponent
import proton.android.authenticator.shared.ui.domain.components.images.ImageComponent
import proton.android.authenticator.shared.ui.domain.components.progress.ProgressComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Suppress("MagicNumber")
data class EntryCardContent(
    private val imageUrl: String,
    private val name: UiText,
    private val label: UiText,
    private val currentCode: UiText,
    private val nextCode: UiText,
    private val remainingSeconds: Int,
    private val totalSeconds: Int,
    private val onClick: () -> Unit
) : Content {

    @Composable
    override fun Render() {
        ContainerComponent.Vertical(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = 16.dp))
                .border(
                    width = 1.dp,
                    color = Color.Blue,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .background(color = Color(0x26213E66))
                .clickable { onClick() }
                .padding(all = ThemePadding.Medium),
            contents = {
                listOf(
                    ContainerComponent.Horizontal(
                        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                        verticalAlignment = Alignment.CenterVertically,
                        contents = {
                            listOf(
                                ImageComponent.Network(
                                    modifier = Modifier
                                        .size(size = 40.dp)
                                        .clip(shape = RoundedCornerShape(size = 8.dp)),
                                    url = imageUrl
                                ),
                                ContainerComponent.Vertical(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(weight = 1f, fill = true),
                                    contents = {
                                        listOf(
                                            TextComponent.Title(
                                                text = name
                                            ),
                                            TextComponent.Title(
                                                text = label
                                            )
                                        )
                                    }
                                ),
                                ProgressComponent.CircularCounter(
                                    current = remainingSeconds,
                                    total = totalSeconds
                                )
                            )
                        }
                    ),
                    DividerComponent.DoubleHorizontal(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = ThemePadding.Small)
                    ),
                    ContainerComponent.Horizontal(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        contents = {
                            listOf(
                                TextComponent.Title(
                                    modifier = Modifier.weight(weight = 1f, fill = true),
                                    text = currentCode
                                ),
                                ContainerComponent.Vertical(
                                    horizontalAlignment = Alignment.End,
                                    contents = {
                                        listOf(
                                            TextComponent.Title(
                                                text = UiText.Dynamic(value = "Next")
                                            ),
                                            TextComponent.Title(
                                                text = nextCode
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    )
                )
            }
        ).Render()
    }

}
