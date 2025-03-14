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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeBottomBar(onEntryQueryChange: (String) -> Unit, onNewEntryClick: () -> Unit) {
    var searchQuery = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Theme.colorScheme.gradientBackgroundColor3.copy(alpha = 0.97f)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = ThemeSpacing.Medium,
                    top = ThemeSpacing.Medium,
                    end = ThemeSpacing.Medium,
                    bottom = ThemeSpacing.Large
                ),
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(weight = 1f, fill = true),
                shape = RoundedCornerShape(size = 32.dp),
                value = searchQuery.value,
                onValueChange = { newSearchQuery ->
                    searchQuery.value = newSearchQuery
                    onEntryQueryChange(newSearchQuery)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_magnifier),
                        contentDescription = null,
                        tint = Theme.colorScheme.textWeak
                    )
                },
                placeholder = {
                    Text(
                        text = "Search",
                        color = Theme.colorScheme.textWeak
                    )
                }
            )

            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Theme.colorScheme.gradientButtonColor1,
                                Theme.colorScheme.gradientButtonColor2
                            )
                        )
                    ),
                onClick = onNewEntryClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Theme.colorScheme.textNorm
                )
            }
        }
    }
}
