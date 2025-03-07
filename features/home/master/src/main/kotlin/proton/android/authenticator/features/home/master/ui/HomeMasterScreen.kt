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

import proton.android.authenticator.features.home.master.presentation.HomeMasterState
import proton.android.authenticator.shared.ui.contents.bars.AppTopBarContent
import proton.android.authenticator.shared.ui.contents.bars.SearchBottomBarContent
import proton.android.authenticator.shared.ui.contents.entries.EntryCardContent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.models.UiTextMask
import proton.android.authenticator.shared.ui.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.R as uiR

internal class HomeMasterScreen(
    state: HomeMasterState,
    onEntryClick: (entryId: String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit
) : ScaffoldScreen() {

    override val topBarContent: Content? = AppTopBarContent(
        id = "TopBar",
        title = UiText.Dynamic("Authenticator"),
        onActionClick = onSettingsClick
    )

    override val bodyContents: List<Content> = state.entries.map { entry ->
        EntryCardContent(
            id = entry.id.toString(),
            imageUrl = "https://www.amazon.com/favicon.ico",
            name = UiText.Dynamic(value = entry.name),
            label = UiText.Dynamic(value = "amazon@email.com"),
            currentCode = UiText.Dynamic(
                value = entry.currentCode,
                masks = listOf(UiTextMask.Totp)
            ),
            nextCode = UiText.Dynamic(
                value = entry.nextCode,
                masks = listOf(UiTextMask.Totp)
            ),
            remainingSeconds = entry.remainingSeconds,
            totalSeconds = entry.totalSeconds,
            onClick = { onEntryClick("Entry 1") }
        )
    }

    override val bottomBarContent: Content? = SearchBottomBarContent(
        id = "SearchBar",
        query = "",
        leadingIcon = UiIcon.Resource(resId = uiR.drawable.ic_settings),
        onLeadingIconClick = onSettingsClick,
        trailingIcon = UiIcon.Resource(resId = uiR.drawable.ic_plus),
        onTrailingIconClick = onAddClick
    )
}
