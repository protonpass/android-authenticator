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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.components.lists.DraggableVerticalList
import proton.android.authenticator.shared.ui.domain.components.refresh.PullToRefresh
import proton.android.authenticator.shared.ui.domain.models.UiDraggableItem
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntries(
    state: HomeMasterState.Ready,
    listState: LazyListState,
    onCopyEntryCodeClick: (HomeMasterEntryModel, Boolean) -> Unit,
    onEditEntryClick: (HomeMasterEntryModel) -> Unit,
    onDeleteEntryClick: (HomeMasterEntryModel) -> Unit,
    onEntriesSorted: (Map<String, Int>, List<HomeMasterEntryModel>) -> Unit,
    onEntriesRefreshPull: (Boolean, List<HomeMasterEntryModel>) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        entryModels.map { entryModel ->
            UiDraggableItem(
                id = entryModel.id,
                content = {
                    HomeEntry(
                        animateOnCodeChange = animateOnCodeChange,
                        searchQuery = searchQuery,
                        showBoxesInCode = showBoxesInCode,
                        themeType = themeType,
                        entryModel = entryModel,
                        entryCodeMasks = entryCodeMasks,
                        remainingSeconds = getRemainingSeconds(entryModel.totalSeconds),
                        onCopyCodeClick = { onCopyEntryCodeClick(entryModel, areCodesHidden) },
                        onEditClick = { onEditEntryClick(entryModel) },
                        onDeleteClick = { onDeleteEntryClick(entryModel) }
                    )
                }
            )
        }.also { items ->
            PullToRefresh(
                modifier = modifier,
                isRefreshing = isRefreshing,
                onRefresh = { onEntriesRefreshPull(isSyncEnabled, entryModels) }
            ) {
                DraggableVerticalList(
                    modifier = Modifier.fillMaxSize(),
                    draggableItems = items,
                    listState = listState,
                    verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                    onSorted = { sortingMap -> onEntriesSorted(sortingMap, entryModels) }
                )
            }
        }
    }
}
