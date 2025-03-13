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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.dividers.DividerComponent
import proton.android.authenticator.shared.ui.domain.components.images.ImageComponent
import proton.android.authenticator.shared.ui.domain.components.progress.ProgressComponent
import proton.android.authenticator.shared.ui.domain.components.swipes.SwipeComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.containerSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

data class EntryCardContent(
    override val renderId: String,
    private val id: String,
    private val imageUrl: String,
    private val issuer: UiText,
    private val name: UiText,
    private val currentCode: UiText,
    private val nextCode: UiText,
    private val remainingSeconds: Int,
    private val totalSeconds: Int,
    private val isRevealed: Boolean,
    private val onClick: (String) -> Unit,
    private val onEditClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : Content {

    @Composable
    override fun Render() {
        SwipeComponent.Actions(
            renderId = renderId,
            isRevealed = isRevealed,
            actions = {
                listOf(
                    ContainerComponent.Vertical(
                        renderId = "$renderId-actions",
                        modifier = Modifier
                            .fillMaxHeight()
                            .containerSection()
                            .padding(all = ThemePadding.Medium),
                        contents = {
                            listOf(
                                TextComponent.Standard(
                                    renderId = "$renderId-edit",
                                    modifier = Modifier
                                        .weight(1f, fill = true)
                                        .clickable { onEditClick(id) },
                                    text = UiText.Dynamic("Edit"),
                                    textAlign = TextAlign.Center,
                                    color = { Theme.colorScheme.textNorm },
                                    style = { Theme.typography.body1Medium }
                                ),
                                TextComponent.Standard(
                                    renderId = "$renderId-delete",
                                    modifier = Modifier
                                        .weight(1f, fill = true)
                                        .clickable { onDeleteClick(id) },
                                    text = UiText.Dynamic("Delete"),
                                    color = { Theme.colorScheme.textNorm },
                                    style = { Theme.typography.body1Medium }
                                )
                            )
                        }
                    )
                )
            },
            content = ContainerComponent.Vertical(
                renderId = "$renderId-content",
                modifier = Modifier
                    .fillMaxWidth()
                    .containerSection()
                    .clickable { onClick(id) },
                contents = {
                    listOf(
                        ContainerComponent.Horizontal(
                            renderId = "$renderId-horizontal",
                            modifier = Modifier.padding(
                                start = ThemePadding.Medium,
                                top = ThemePadding.Medium,
                                end = ThemePadding.Medium
                            ),
                            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                            verticalAlignment = Alignment.CenterVertically,
                            contents = {
                                listOf(
                                    ImageComponent.Network(
                                        renderId = "$renderId-image",
                                        modifier = Modifier
                                            .size(size = 30.dp)
                                            .clip(shape = RoundedCornerShape(size = 8.dp)),
                                        url = imageUrl
                                    ),
                                    ContainerComponent.Vertical(
                                        renderId = "$renderId-vertical",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(weight = 1f, fill = true),
                                        contents = {
                                            listOf(
                                                TextComponent.Standard(
                                                    renderId = "$renderId-name",
                                                    text = issuer,
                                                    color = { Theme.colorScheme.textNorm },
                                                    style = { Theme.typography.body1Regular }
                                                ),
                                                TextComponent.Standard(
                                                    renderId = "$renderId-label",
                                                    text = name,
                                                    color = { Theme.colorScheme.textWeak },
                                                    style = { Theme.typography.body2Regular }
                                                )
                                            )
                                        }
                                    ),
                                    ProgressComponent.CircularCounter(
                                        renderId = "$renderId-progress",
                                        modifier = Modifier.size(size = 36.dp),
                                        current = remainingSeconds,
                                        total = totalSeconds
                                    )
                                )
                            }
                        ),
                        DividerComponent.DoubleHorizontal(
                            renderId = "$renderId-divider",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = ThemePadding.Small),
                            topColor = Color.Black.copy(alpha = 0.2f),
                            bottomColor = Color.White.copy(alpha = 0.12f)
                        ),
                        ContainerComponent.Horizontal(
                            renderId = "$renderId-horizontal",
                            modifier = Modifier.padding(
                                start = ThemePadding.Medium,
                                end = ThemePadding.Medium,
                                bottom = ThemePadding.Medium
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            contents = {
                                listOf(
                                    TextComponent.Totp(
                                        renderId = "$renderId-totp",
                                        modifier = Modifier.weight(weight = 1f, fill = true),
                                        text = currentCode,
                                        color = { Theme.colorScheme.textNorm },
                                        style = {
                                            Theme.typography.monoMedium1
                                                .copy(shadow = ThemeShadow.TextDefault)
                                        }
                                    ),
                                    ContainerComponent.Vertical(
                                        renderId = "$renderId-vertical",
                                        verticalArrangement = Arrangement.spacedBy(
                                            space = ThemeSpacing.ExtraSmall
                                        ),
                                        horizontalAlignment = Alignment.End,
                                        contents = {
                                            listOf(
                                                TextComponent.Standard(
                                                    renderId = "$renderId-next",
                                                    modifier = Modifier.padding(end = ThemePadding.Small),
                                                    text = UiText.Dynamic(value = "Next"),
                                                    color = { Theme.colorScheme.textWeak },
                                                    style = {
                                                        Theme.typography.body1Regular
                                                            .copy(shadow = ThemeShadow.TextDefault)
                                                    }
                                                ),
                                                TextComponent.Standard(
                                                    renderId = "$renderId-next-code",
                                                    text = nextCode,
                                                    color = { Theme.colorScheme.textNorm },
                                                    style = {
                                                        Theme.typography.monoMedium2
                                                            .copy(shadow = ThemeShadow.TextDefault)
                                                    }
                                                )
                                            )
                                        }
                                    )
                                )
                            }
                        )
                    )
                }
            )
        ).Render()

    }

}
