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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.components.lists.DraggableVerticalList
import proton.android.authenticator.shared.ui.domain.models.UiDraggableItem
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntries(
    state: HomeMasterState.Ready,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onCopyEntryCodeClick: (HomeMasterEntryModel) -> Unit,
    onEditEntryClick: (HomeMasterEntryModel) -> Unit,
    onDeleteEntryClick: (HomeMasterEntryModel) -> Unit,
    onEntryRearranged: (String, Int, String, Int, Map<String, HomeMasterEntryModel>) -> Unit,
    modifier: Modifier = Modifier
) {
    with(state) {
        entryModels.map { entryModel ->
            UiDraggableItem(
                id = entryModel.id,
                content = {
                    HomeEntry(
                        animateOnCodeChange = animateOnCodeChange,
                        showBoxesInCode = showBoxesInCode,
                        themeType = themeType,
                        entryModel = entryModel,
                        entryCodeMasks = entryCodeMasks,
                        remainingSeconds = getRemainingSeconds(entryModel.totalSeconds),
                        onCopyCodeClick = { onCopyEntryCodeClick(entryModel) },
                        onEditClick = { onEditEntryClick(entryModel) },
                        onDeleteClick = { onDeleteEntryClick(entryModel) }
                    )
                }
            )
        }.also { items ->
            DraggableVerticalList(
                modifier = modifier,
                draggableItems = items,
                listState = listState,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
                onMoved = { fromIndex, fromId, toIndex, toId ->
                    onEntryRearranged(fromId, fromIndex, toId, toIndex, entryModelsMap)
                }
            )
        }
    }
}
