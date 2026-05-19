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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.features.home.master.presentation.HomeMasterEvent
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.modifiers.applyIf
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewContainer
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewProvider

@Composable
internal fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeMasterState,
    listState: LazyListState,
    onNewEntryClick: () -> Unit,
    onImportEntriesClick: () -> Unit,
    onCopyEntryCodeClick: (String) -> Unit,
    onEditEntryClick: (String) -> Unit,
    onDeleteEntryClick: (String) -> Unit,
    onEntriesSorted: (Map<String, Int>) -> Unit,
    onRefreshEntries: (Boolean) -> Unit
) {
    when (state) {
        is HomeMasterState.Empty -> {
            HomeEmpty(
                modifier = modifier.fillMaxSize(),
                state = state,
                onNewEntryClick = onNewEntryClick,
                onImportEntriesClick = onImportEntriesClick,
                onEntriesRefreshPull = onRefreshEntries
            )
        }

        is HomeMasterState.EmptySearch -> {
            HomeEmptySearch(
                modifier = modifier
                    .fillMaxSize()
                    .applyIf(
                        condition = !state.showBottomBar,
                        ifTrue = { imePadding() }
                    )
            )
        }

        is HomeMasterState.Loading -> {
            HomeLoading(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = ThemePadding.Medium),
                state = state
            )
        }

        is HomeMasterState.Ready -> {
            HomeEntries(
                modifier = modifier.padding(horizontal = ThemePadding.Medium),
                state = state,
                listState = listState,
                onCopyEntryCodeClick = onCopyEntryCodeClick,
                onEditEntryClick = onEditEntryClick,
                onDeleteEntryClick = onDeleteEntryClick,
                onEntriesSorted = onEntriesSorted,
                onEntriesRefreshPull = onRefreshEntries
            )
        }
    }
}

@Preview
@Composable
fun HomeContentEmptyPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    ThemePreviewContainer(isDark = isDark) {
        HomeContent(
            state = HomeMasterState.Empty(
                event = HomeMasterEvent.Idle,
                isRefreshing = false,
                settings = Settings.Default
            ),
            listState = rememberLazyListState(),
            onNewEntryClick = {},
            onImportEntriesClick = {},
            onCopyEntryCodeClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {},
            onEntriesSorted = {},
            onRefreshEntries = {}
        )
    }
}

@Preview
@Composable
fun HomeContentReadyPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    ThemePreviewContainer(isDark = isDark) {
        HomeContent(
            state = HomeMasterState.Ready(
                event = HomeMasterEvent.Idle,
                searchQuery = "",
                isRefreshing = false,
                entryModelsMap = HomeMasterEntryModelPreviewProvider.sampleEntries,
                entryCodesRemainingTimes = HomeMasterEntryModelPreviewProvider.sampleRemainingTimes,
                settings = Settings.Default
            ),
            listState = rememberLazyListState(),
            onNewEntryClick = {},
            onImportEntriesClick = {},
            onCopyEntryCodeClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {},
            onEntriesSorted = {},
            onRefreshEntries = {}
        )
    }
}
