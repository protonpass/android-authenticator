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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.bars.BarComponent
import proton.android.authenticator.shared.ui.domain.components.icons.IconComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundTopBarGradient
import proton.android.authenticator.shared.ui.domain.modifiers.dropShadow
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

data class AppTopBarContent(
    private val title: UiText,
    private val onActionClick: () -> Unit
) : Content {

    @Composable
    override fun Render() {
        BarComponent.TopApp(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = ThemePadding.Medium)
                .backgroundTopBarGradient(),
            title = TextComponent.Standard(
                text = title,
                color = { Theme.colorScheme.textNorm },
                style = { Theme.typography.title }
            ),
            actions = listOf(
                IconComponent.Descriptive(
                    modifier = Modifier
                        .dropShadow(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.2f),
                            blur = 4.dp,
                            offsetX = 0.dp,
                            offsetY = 2.dp
                        )
                        .clip(shape = CircleShape)
                        .border(
                            shape = CircleShape,
                            width = ThemeThickness.Small,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.01f)
                                )
                            )
                        )
                        .background(color = Color.White.copy(alpha = 0.12f))
                        .clickable { onActionClick() }
                        .padding(all = ThemePadding.Small),
                    icon = UiIcon.Resource(resId = R.drawable.ic_settings_alt),
                    tint = { Theme.colorScheme.textNorm }
                )
            )
        ).Render()
    }

}
