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

package proton.android.authenticator.shared.ui.contents.banners

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.components.buttons.ButtonComponent
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.icons.IconComponent
import proton.android.authenticator.shared.ui.domain.components.images.ImageComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiImage
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.containerBanner
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

data class PromoBannerContent(
    override val renderId: String,
    private val title: UiText,
    private val description: UiText,
    private val actionText: UiText,
    private val onActionClick: () -> Unit,
    private val onDismissClick: () -> Unit
) : Content {

    @Composable
    override fun Render() {
        ContainerComponent.Box(
            renderId = renderId,
            modifier = Modifier
                .fillMaxWidth()
                .containerBanner()
                .padding(all = ThemePadding.Medium),
            contents = {
                listOf(
                    ImageComponent.Local(
                        renderId = "$renderId-image",
                        modifier = Modifier
                            .align(alignment = Alignment.BottomEnd)
                            .size(size = 180.dp)
                            .offset(x = ThemeSpacing.Large, y = ThemeSpacing.Medium),
                        image = UiImage.Resource(resId = uiR.drawable.pass_preview)
                    ),
                    ContainerComponent.Vertical(
                        renderId = "$renderId-vertical",
                        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
                        contents = {
                            listOf(
                                ContainerComponent.Box(
                                    renderId = "$renderId-box",
                                    modifier = Modifier.fillMaxWidth(),
                                    contents = {
                                        listOf(
                                            ImageComponent.Local(
                                                renderId = "$renderId-logo",
                                                modifier = Modifier.align(alignment = Alignment.CenterStart),
                                                image = UiImage.Resource(resId = uiR.drawable.ic_logo_pass_36)
                                            ),
                                            IconComponent.Actionable(
                                                renderId = "$renderId-close",
                                                modifier = Modifier
                                                    .align(alignment = Alignment.TopEnd)
                                                    .clip(shape = CircleShape),
                                                icon = UiIcon.Resource(resId = uiR.drawable.ic_cross_circle_filled),
                                                onClick = onDismissClick,
                                                tint = {
                                                    Theme.colorScheme.textNorm.copy(alpha = 0.7f)
                                                }
                                            )
                                        )
                                    }
                                ),
                                ContainerComponent.Horizontal(
                                    renderId = "$renderId-horizontal",
                                    contents = {
                                        listOf(
                                            ContainerComponent.Vertical(
                                                renderId = "$renderId-vertical",
                                                modifier = Modifier.width(width = 180.dp),
                                                verticalArrangement = Arrangement.spacedBy(
                                                    space = ThemeSpacing.ExtraSmall
                                                ),
                                                contents = {
                                                    listOf(
                                                        TextComponent.Standard(
                                                            renderId = "$renderId-title",
                                                            text = title,
                                                            color = { Theme.colorScheme.textNorm },
                                                            style = { Theme.typography.headline }
                                                        ),
                                                        TextComponent.Standard(
                                                            renderId = "$renderId-description",
                                                            text = description,
                                                            color = { Theme.colorScheme.textNorm },
                                                            style = { Theme.typography.body1Regular }
                                                        )
                                                    )
                                                }
                                            )
                                        )
                                    }
                                ),
                                ButtonComponent.Text(
                                    renderId = "$renderId-button",
                                    text = actionText,
                                    onClick = onActionClick,
                                    color = { Theme.colorScheme.interactionPurpleNorm },
                                    textColor = { Theme.colorScheme.textNorm },
                                    textStyle = { Theme.typography.body2Medium }
                                )
                            )
                        }
                    )
                )
            }
        ).Render()
    }

}
