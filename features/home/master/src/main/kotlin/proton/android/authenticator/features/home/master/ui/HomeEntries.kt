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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.features.home.master.presentation.HomeMasterEvent
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.components.lists.DraggableVerticalList
import proton.android.authenticator.shared.ui.domain.components.refresh.PullToRefresh
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewContainer
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewProvider
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntries(
    state: HomeMasterState.Ready,
    listState: LazyListState,
    onCopyEntryCodeClick: (String) -> Unit,
    onEditEntryClick: (String) -> Unit,
    onDeleteEntryClick: (String) -> Unit,
    onEntriesSorted: (Map<String, Int>) -> Unit,
    onEntriesRefreshPull: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        PullToRefresh(
            modifier = modifier,
            isRefreshing = isRefreshing,
            onRefresh = { onEntriesRefreshPull(isSyncEnabled) }
        ) {
            DraggableVerticalList(
                modifier = Modifier.fillMaxSize(),
                draggableItems = draggableItems,
                isDragEnabled = canSortItems,
                listState = listState,
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                onSorted = onEntriesSorted,
                needsBottomExtraSpace = state.needsBottomExtraSpace
            ) { item ->
                entryModel(item.id)?.let { entryModel ->
                    HomeEntry(
                        animateOnCodeChange = animateOnCodeChange,
                        searchQuery = searchQuery,
                        showBoxesInCode = showBoxesInCode,
                        themeType = themeType,
                        entryModel = entryModel,
                        entryCodeMasks = entryCodeMasks,
                        remainingSeconds = getRemainingSeconds(entryModel.totalSeconds),
                        onCopyCodeClick = { onCopyEntryCodeClick(entryModel.id) },
                        onEditClick = { onEditEntryClick(entryModel.id) },
                        onDeleteClick = { onDeleteEntryClick(entryModel.id) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeEntriesReadyPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    ThemePreviewContainer(isDark = isDark) {
        HomeEntries(
            state = HomeMasterState.Ready(
                event = HomeMasterEvent.Idle,
                searchQuery = "",
                isRefreshing = false,
                entryModelsMap = HomeMasterEntryModelPreviewProvider.sampleEntries,
                entryCodesRemainingTimes = HomeMasterEntryModelPreviewProvider.sampleRemainingTimes,
                settings = Settings.Default
            ),
            listState = rememberLazyListState(),
            onCopyEntryCodeClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {},
            onEntriesSorted = {},
            onEntriesRefreshPull = {}
        )
    }
}

@Preview
@Composable
fun HomeEntriesSearchPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    ThemePreviewContainer(isDark = isDark) {
        HomeEntries(
            state = HomeMasterState.Ready(
                event = HomeMasterEvent.Idle,
                searchQuery = "Proton",
                isRefreshing = false,
                entryModelsMap = HomeMasterEntryModelPreviewProvider.sampleEntries
                    .filterValues { it.issuer == "Proton Mail" },
                entryCodesRemainingTimes = HomeMasterEntryModelPreviewProvider.sampleRemainingTimes,
                settings = Settings.Default
            ),
            listState = rememberLazyListState(),
            onCopyEntryCodeClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {},
            onEntriesSorted = {},
            onEntriesRefreshPull = {}
        )
    }
}
