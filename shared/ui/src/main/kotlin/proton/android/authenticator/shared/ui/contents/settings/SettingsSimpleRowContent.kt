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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.icons.IconComponent
import proton.android.authenticator.shared.ui.domain.components.images.ImageComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

data class SettingsSimpleRowContent(
    override val renderId: String,
    private val title: UiText,
    private val icon: UiIcon? = null
) : SettingsRowContent {

    @Composable
    override fun Render() {
        ContainerComponent.Horizontal(
            renderId = renderId,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ThemePadding.Medium),
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            contents = {
                buildList {
                    if (icon != null) {
                        ImageComponent.Icon(
                            renderId = "$renderId-icon",
                            icon = icon
                        ).also(::add)
                    }

                    TextComponent.Standard(
                        renderId = "$renderId-title",
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = title,
                        color = { Theme.colorScheme.textNorm },
                        style = { Theme.typography.body1Regular }
                    ).also(::add)

                    IconComponent.Descriptive(
                        renderId = "$renderId-chevron",
                        icon = UiIcon.Resource(resId = R.drawable.ic_chevron_tiny_right),
                        tint = { Theme.colorScheme.textWeak }
                    ).also(::add)
                }
            }
        ).Render()
    }

}
