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

package proton.android.authenticator.features.home.master.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.modifiers.shimmerEffect
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntryCardPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = ThemeRadius.Medium))
                .backgroundSection(applyShadow = true)
                .padding(
                    start = ThemePadding.Medium,
                    top = ThemePadding.Medium,
                    end = ThemePadding.Medium
                ),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(size = ThemeSpacing.Large)
                        .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                        .shimmerEffect()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = ThemePadding.ExtraLarge)
                            .height(height = ThemeSpacing.Medium)
                            .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                            .shimmerEffect()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = ThemePadding.ExtraLarge)
                            .height(height = ThemeSpacing.Small)
                            .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                            .shimmerEffect()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(size = ThemeSpacing.Large)
                        .clip(shape = CircleShape)
                        .shimmerEffect()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ThemePadding.Small)
                    .height(height = ThemeSpacing.ExtraSmall.div(2))
                    .shimmerEffect()
            )

            Row(
                modifier = Modifier.padding(vertical = ThemePadding.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .height(height = ThemeSpacing.Large)
                        .padding(end = ThemePadding.ExtraLarge)
                        .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                        .shimmerEffect()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .width(width = ThemeSpacing.Large)
                            .height(height = ThemeSpacing.Medium)
                            .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                            .shimmerEffect()
                    )

                    Box(
                        modifier = Modifier
                            .width(width = ThemeSpacing.ExtraLarge)
                            .height(height = ThemeSpacing.Medium)
                            .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
