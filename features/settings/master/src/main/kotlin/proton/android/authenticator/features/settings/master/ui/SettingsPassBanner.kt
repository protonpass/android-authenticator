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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundBlur
import proton.android.authenticator.shared.ui.domain.modifiers.containerBanner
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun SettingsPassBanner(onDismissClick: () -> Unit, onActionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 222.dp)
            .containerBanner()
    ) {
        Image(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .size(size = 222.dp)
                .offset(x = ThemeSpacing.Large.plus(ThemeSpacing.Medium)),
            painter = painterResource(id = uiR.drawable.preview_pass),
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ThemePadding.Medium),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    text = stringResource(id = R.string.settings_discover_pass_title),
                    color = Theme.colorScheme.white,
                    style = Theme.typography.headline
                )

                Icon(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable(onClick = onDismissClick),
                    painter = painterResource(id = uiR.drawable.ic_cross_circle_filled),
                    contentDescription = null,
                    tint = Theme.colorScheme.whiteAlpha70
                )
            }

            Text(
                modifier = Modifier.width(width = 240.dp),
                text = stringResource(id = R.string.settings_pass_banner_description),
                color = Theme.colorScheme.white,
                style = Theme.typography.body2Regular
            )

            SettingsPassBannerPassItemIcons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ThemePadding.Small)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundBlur(
                    backgroundColor = Theme.colorScheme.blackAlpha20,
                    blurRadius = 44.dp
                )
                .padding(
                    start = ThemePadding.Medium,
                    top = ThemePadding.ExtraSmall,
                    end = ThemePadding.Medium,
                    bottom = ThemePadding.Small
                )
                .align(alignment = Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(weight = 1f, fill = true),
                horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = ThemeRadius.MediumSmall))
                        .background(color = Theme.colorScheme.white)
                        .padding(all = ThemePadding.Small),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(size = 24.dp),
                        painter = painterResource(id = uiR.drawable.ic_logo_pass_36),
                        contentDescription = null
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_discover_pass_title),
                        color = Theme.colorScheme.white,
                        style = Theme.typography.body3Bold
                    )

                    Text(
                        text = stringResource(id = uiR.string.action_free),
                        color = Theme.colorScheme.white,
                        style = Theme.typography.body3Regular
                    )
                }
            }

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Theme.colorScheme.interactionPurpleNorm
                )
            ) {
                Text(
                    text = stringResource(uiR.string.action_get).uppercase(),
                    color = Theme.colorScheme.white,
                    style = Theme.typography.body3Bold
                )
            }
        }
    }
}
