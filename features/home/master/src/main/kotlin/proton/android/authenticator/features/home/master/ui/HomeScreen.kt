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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.features.home.master.presentation.HomeMasterViewModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.fabs.IconFloatingActionButton
import proton.android.authenticator.shared.ui.domain.components.textfields.SearchTextField
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundAppBar
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.isDarkTheme

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    onEditEntryClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onNewEntryClick: () -> Unit,
    onImportEntriesClick: () -> Unit
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
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        snackbarHostState = snackbarHostState,
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
                if (state is HomeMasterState.Ready) {
                    SetNavigationBarColor(
                        color = Theme.colorScheme.backgroundGradientBottom.copy(alpha = 0.97f),
                        useDarkIcons = !isDarkTheme((state as HomeMasterState.Ready).themeType)
                    )
                }
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
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            listState = lazyListState,
            onNewEntryClick = onNewEntryClick,
            onImportEntriesClick = onImportEntriesClick,
            onEditEntryClick = { entry -> onEditEntryClick(entry.id) },
            onCopyEntryCodeClick = ::onCopyEntryCode,
            onDeleteEntryClick = ::onDeleteEntry,
            onRearrangeEntry = ::onRearrangeEntry
        )
    }
}
