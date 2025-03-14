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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.shared.ui.domain.modifiers.containerSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Composable
internal fun HomeEntry(entryModel: HomeMasterEntryModel, onClick: (entryModel: HomeMasterEntryModel) -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick(entryModel) }
            .fillMaxWidth()
            .containerSection()
    ) {
        Row(
            modifier = Modifier.padding(
                start = ThemePadding.Medium,
                top = ThemePadding.Medium,
                end = ThemePadding.Medium
            ),
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(size = 8.dp))
                    .background(color = Theme.colorScheme.interactionPurpleStrong)
                    .padding(
                        horizontal = ThemePadding.MediumSmall,
                        vertical = ThemePadding.Small
                    ),
                text = entryModel.issuer.first().toString(),
                style = Theme.typography.headline
                    .copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Theme.colorScheme.gradientBannerColor10,
                                Theme.colorScheme.gradientBannerColor8,
                                Theme.colorScheme.gradientBannerColor4,
                                Theme.colorScheme.gradientBannerColor2
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = entryModel.issuer,
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.body1Regular
                        .copy(shadow = ThemeShadow.TextDefault)
                )

                Text(
                    text = entryModel.name,
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.body2Regular
                        .copy(shadow = ThemeShadow.TextDefault)
                )
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 36.dp),
                    color = Theme.colorScheme.inputBorderFocused,
                    trackColor = Theme.colorScheme.inputBorderFocused.copy(alpha = 0.2f),
                    progress = {
                        entryModel.remainingSeconds.toFloat() / entryModel.totalSeconds.toFloat()
                    },
                    gapSize = 0.dp
                )

                Text(
                    text = entryModel.remainingSeconds.toString(),
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.compactMedium
                        .copy(shadow = ThemeShadow.TextDefault)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = ThemePadding.Small),
            thickness = ThemeThickness.Small,
            color = Color.Black.copy(alpha = 0.2f)
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = ThemePadding.Small),
            thickness = ThemeThickness.Small,
            color = Color.White.copy(alpha = 0.12f)
        )

        Row(
            modifier = Modifier.padding(
                start = ThemePadding.Medium,
                end = ThemePadding.Medium,
                bottom = ThemePadding.Medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f, fill = true),
                text = entryModel.currentCode,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.monoMedium1
                    .copy(shadow = ThemeShadow.TextDefault)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Next",
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.body1Regular
                        .copy(shadow = ThemeShadow.TextDefault)
                )

                Text(
                    text = entryModel.nextCode,
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.monoMedium2
                        .copy(shadow = ThemeShadow.TextDefault)
                )
            }
        }
    }
}
