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

package proton.android.authenticator.shared.ui.domain.components.bars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme


@[Composable OptIn(ExperimentalMaterial3Api::class)]
fun SmallTopBar(
    title: UiText,
    navigationIcon: UiIcon,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    action: UiText? = null,
    isActionEnabled: Boolean = false,
    onActionClick: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title.asString(),
                style = Theme.typography.body1Medium
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationClick
            ) {
                Icon(
                    painter = navigationIcon.asPainter(),
                    contentDescription = null,
                    tint = Theme.colorScheme.textNorm
                )
            }
        },
        actions = {
            action?.let { text ->
                TextButton(
                    enabled = isActionEnabled,
                    onClick = onActionClick
                ) {
                    Text(
                        text = action.asString(),
                        color = Theme.colorScheme.accent,
                        style = Theme.typography.body1Medium
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Theme.colorScheme.textNorm
        )
    )
}
