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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.home.master.presentation.HomeMasterViewModel
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen

@Composable
fun HomeScreen(
    onEditEntryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNewEntryClick: () -> Unit
) = with(hiltViewModel<HomeMasterViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    ScaffoldScreen(
        topBar = {
            HomeTopBar(onSettingsClick = onSettingsClick)
        },
        bottomBar = {
            if (state.hasEntryModels) {
                HomeBottomBar(
                    onEntryQueryChange = ::onUpdateEntrySearchQuery,
                    onNewEntryClick = onNewEntryClick
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            paddingValues = paddingValues,
            state = state,
            onNewEntryClick = onNewEntryClick,
            onEditEntryClick = { entry -> onEditEntryClick(entry.id) },
            onCopyEntryCodeClick = ::onCopyEntryCode,
            onDeleteEntryClick = ::onDeleteEntry
        )
    }
}
