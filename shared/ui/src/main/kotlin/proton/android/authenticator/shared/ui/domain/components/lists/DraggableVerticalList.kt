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

package proton.android.authenticator.shared.ui.domain.components.lists

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.models.UiDraggableItem
import proton.android.authenticator.shared.ui.domain.modifiers.applyIf
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun DraggableVerticalList(
    draggableItems: List<UiDraggableItem>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(all = ThemePadding.None),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    highlightColor: Color = Theme.colorScheme.inputBorderFocused,
    onMoved: (Int, String, Int, String) -> Unit
) {
    var items by remember(key1 = draggableItems) { mutableStateOf(draggableItems) }

    var selectedItemId by remember { mutableStateOf<String?>(null) }

    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }

    var fromItem by remember { mutableStateOf<LazyListItemInfo?>(null) }

    var toItem by remember { mutableStateOf<LazyListItemInfo?>(null) }

    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = listState
    ) { from, to ->
        fromItem = from
        toItem = to

        items = items.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        itemsIndexed(
            items = items,
            key = { index, item -> item.id }
        ) { index, item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = item.id
            ) {
                Box(
                    modifier = Modifier
                        .applyIf(
                            condition = selectedItemId == item.id,
                            ifTrue = {
                                border(
                                    shape = RoundedCornerShape(size = ThemeRadius.MediumSmall),
                                    width = ThemeThickness.Small,
                                    color = highlightColor
                                )
                            }
                        )
                        .longPressDraggableHandle(
                            onDragStarted = {
                                fromItem = null
                                toItem = null

                                selectedItemId = item.id
                                selectedItemIndex = index
                            },
                            onDragStopped = {
                                fromItem?.let { from ->
                                    toItem?.let { to ->
                                        selectedItemIndex?.let { selectedIndex ->
                                            if (selectedIndex == to.index) {
                                                items = draggableItems
                                            } else {
                                                onMoved(
                                                    selectedIndex,
                                                    from.key.toString(),
                                                    to.index,
                                                    to.key.toString()
                                                )
                                            }
                                        }
                                    }
                                }

                                selectedItemId = null
                                selectedItemIndex = null
                            }
                        )
                ) {
                    item.content()
                }
            }
        }
    }
}
