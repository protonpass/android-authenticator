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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.features.home.master.R
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundActionButton
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@[Composable OptIn(ExperimentalMaterial3Api::class)]
internal fun HomeTopBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = stringResource(id = R.string.home_screen_title),
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.title
            )
        },
        actions = {
            Box(
                modifier = Modifier.padding(end = ThemePadding.MediumSmall)
            ) {
                Icon(
                    modifier = Modifier
                        .backgroundActionButton()
                        .clickable(onClick = onSettingsClick)
                        .padding(all = ThemePadding.Small),
                    painter = painterResource(uiR.drawable.ic_settings_alt),
                    tint = Theme.colorScheme.textNorm,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = Color.Transparent)
    )
}
