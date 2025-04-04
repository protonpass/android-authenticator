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

package proton.android.authenticator.features.home.scan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.features.home.scan.R
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun HomeScanBottomBar(
    onCloseClick: () -> Unit,
    onEnterManuallyClick: () -> Unit,
    onOpenGalleryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(
                start = ThemePadding.Medium,
                end = ThemePadding.Medium,
                bottom = ThemePadding.Large
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCloseClick
        ) {
            Icon(
                painter = painterResource(id = uiR.drawable.ic_cross_big),
                contentDescription = null,
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier.weight(weight = 1f, fill = true),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onEnterManuallyClick
            ) {
                Text(
                    text = stringResource(id = R.string.home_scan_enter_manually),
                    color = Color.White
                )
            }
        }

        IconButton(
            onClick = onOpenGalleryClick
        ) {
            Icon(
                painter = painterResource(id = uiR.drawable.ic_image),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
