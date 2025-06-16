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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
internal fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeMasterState,
    listState: LazyListState,
    onNewEntryClick: () -> Unit,
    onCopyEntryCodeClick: (HomeMasterEntryModel, Boolean) -> Unit,
    onEditEntryClick: (HomeMasterEntryModel) -> Unit,
    onDeleteEntryClick: (HomeMasterEntryModel) -> Unit,
    onRearrangeEntry: (String, Int, String, Int, Map<String, HomeMasterEntryModel>) -> Unit
) {
    when (state) {
        HomeMasterState.Empty -> {
            HomeEmpty(
                modifier = modifier.fillMaxSize(),
                onNewEntryClick = onNewEntryClick
            )
        }

        is HomeMasterState.Loading -> {
            HomeLoading(
                modifier = modifier.fillMaxSize().padding(horizontal = ThemePadding.Medium),
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
                onEntryRearranged = onRearrangeEntry
            )
        }
    }
}
