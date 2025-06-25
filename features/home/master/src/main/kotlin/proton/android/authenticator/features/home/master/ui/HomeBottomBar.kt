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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.textfields.SearchTextField
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundPrimaryButton
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeBottomBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onEntryQueryChange: (String) -> Unit,
    onNewEntryClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Theme.colorScheme.backgroundGradientBottom.copy(alpha = 0.97f))
            .padding(
                start = ThemePadding.MediumLarge,
                top = ThemePadding.MediumSmall,
                end = ThemePadding.MediumLarge,
                bottom = ThemePadding.Medium
            )
            .imePadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(
                modifier = Modifier.weight(weight = 1f, fill = true),
                value = searchQuery,
                onValueChange = onEntryQueryChange
            )

            AnimatedVisibility(visible = searchQuery.isEmpty()) {
                Icon(
                    modifier = Modifier
                        .backgroundPrimaryButton(blur = 8.dp)
                        .clickable(onClick = onNewEntryClick)
                        .padding(all = ThemePadding.MediumSmall),
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    tint = Theme.colorScheme.white
                )
            }
        }
    }
}
