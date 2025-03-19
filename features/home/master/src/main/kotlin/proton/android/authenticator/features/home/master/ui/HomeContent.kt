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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient

@Composable
internal fun HomeContent(
    state: HomeMasterState,
    onEntryQueryChange: (String) -> Unit,
    onNewEntryClick: () -> Unit,
    onEditEntryClick: (HomeMasterEntryModel) -> Unit,
    onDeleteEntryClick: (HomeMasterEntryModel) -> Unit,
    onSettingsClick: () -> Unit
) = with(state) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        containerColor = Color.Transparent,
        topBar = {
            HomeTopBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            if (hasEntryModels) {
                HomeBottomBar(
                    onEntryQueryChange = onEntryQueryChange,
                    onNewEntryClick = onNewEntryClick
                )
            }
        }
    ) { paddingValues ->
        if (hasEntryModels) {
            HomeEntries(
                paddingValues = paddingValues,
                entryModels = entryModels,
                onEntryClick = onEditEntryClick
            )
        } else {
            HomeEmpty(
                paddingValues = paddingValues,
                onNewEntryClick = onNewEntryClick
            )
        }
    }
}
