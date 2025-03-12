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

package proton.android.authenticator.shared.ui.contents.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.images.ImageComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiImage
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

data class EmptyPlaceholderContent(
    override val id: String,
    private val title: UiText,
    private val subtitle: UiText
) : Content {

    @Composable
    override fun Render() {
        ContainerComponent.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            contents = {
                listOf(
                    ContainerComponent.Vertical(
                        modifier = Modifier.padding(horizontal = ThemePadding.Large),
                        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contents = {
                            listOf(
                                ImageComponent.Local(
                                    image = UiImage.Resource(resId = uiR.drawable.ic_placeholder_saturn)
                                ),
                                TextComponent.Standard(
                                    text = title,
                                    color = { Theme.colorScheme.textNorm },
                                    style = { Theme.typography.monoNorm1 },
                                    textAlign = TextAlign.Center
                                ),
                                TextComponent.Standard(
                                    text = subtitle,
                                    color = { Theme.colorScheme.textNorm },
                                    style = { Theme.typography.monoNorm2 },
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    )
                )
            }
        ).Render()
    }

}
