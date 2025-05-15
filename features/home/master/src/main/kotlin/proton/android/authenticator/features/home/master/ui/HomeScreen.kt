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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.home.master.presentation.HomeMasterViewModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.fabs.IconFloatingActionButton
import proton.android.authenticator.shared.ui.domain.components.textfields.SearchTextField
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundAppBar
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
fun HomeScreen(
    onEditEntryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNewEntryClick: () -> Unit
) = with(hiltViewModel<HomeMasterViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()

    val isBlurred by remember {
        derivedStateOf { lazyListState.firstVisibleItemScrollOffset > 0 }
    }

    LaunchedEffect(key1 = state.searchQuery) {
        lazyListState.scrollToItem(index = 0)
    }

    ScaffoldScreen(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundAppBar(isBlurred = isBlurred)
            ) {
                HomeTopBar(onSettingsClick = onSettingsClick)

                if (state.showTopSearchBar) {
                    SearchTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ThemePadding.Medium,
                                end = ThemePadding.Medium,
                                bottom = ThemePadding.Small
                            ),
                        value = state.searchQuery,
                        onValueChange = ::onUpdateEntrySearchQuery
                    )
                }
            }
        },
        bottomBar = {
            if (state.showBottomBar) {
                HomeBottomBar(
                    searchQuery = state.searchQuery,
                    onEntryQueryChange = ::onUpdateEntrySearchQuery,
                    onNewEntryClick = onNewEntryClick
                )
            }
        },
        fab = {
            if (state.showFabButton) {
                IconFloatingActionButton(
                    icon = UiIcon.Resource(id = R.drawable.ic_plus),
                    onClick = onNewEntryClick
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            state = state,
            listState = lazyListState,
            paddingValues = paddingValues,
            onNewEntryClick = onNewEntryClick,
            onEditEntryClick = { entry -> onEditEntryClick(entry.id) },
            onCopyEntryCodeClick = ::onCopyEntryCode,
            onDeleteEntryClick = ::onDeleteEntry
        )
    }
}
