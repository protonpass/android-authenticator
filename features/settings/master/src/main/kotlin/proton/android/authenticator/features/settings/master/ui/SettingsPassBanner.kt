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

package proton.android.authenticator.features.settings.master.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.domain.modifiers.containerBanner
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun SettingsPassBanner(onDismissClick: () -> Unit, onActionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .containerBanner()
            .padding(all = ThemePadding.Medium)
    ) {
        Image(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .size(size = 180.dp)
                .offset(x = ThemeSpacing.Large, y = ThemeSpacing.Medium),
            painter = painterResource(id = uiR.drawable.pass_preview),
            contentDescription = null
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                    painter = painterResource(id = uiR.drawable.ic_logo_pass_36),
                    contentDescription = null
                )

                Icon(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .clip(shape = CircleShape)
                        .clickable(onClick = onDismissClick),
                    painter = painterResource(id = uiR.drawable.ic_cross_circle_filled),
                    contentDescription = null,
                    tint = Theme.colorScheme.textNorm.copy(alpha = 0.7f)
                )
            }

            Column(
                modifier = Modifier.width(width = 180.dp),
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
            ) {
                Text(
                    text = "Proton Pass",
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.headline
                )

                Text(
                    text = "Free password manager with identity protection.",
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.body1Regular
                )
            }

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Theme.colorScheme.interactionPurpleNorm
                )
            ) {
                Text(
                    text = "Get Proton Pass",
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.body2Medium
                )
            }
        }
    }
}
