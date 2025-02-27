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

import proton.android.authenticator.shared.ui.contents.entries.EntryCardContent
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.screens.ScaffoldScreen

internal class HomeMasterScreen(
    private val onEntryClick: (entryId: String) -> Unit
) : ScaffoldScreen() {

    override val topBarContent: Content? = null

    override val bodyContents: List<Content> = listOf(
        EntryCardContent(
            imageUrl = "https://www.amazon.com/favicon.ico",
            name = UiText.Dynamic(value = "Amazon"),
            label = UiText.Dynamic(value = "amazon@email.com"),
            currentCode = UiText.Dynamic(value = "920827"),
            nextCode = UiText.Dynamic(value = "821200"),
            remainingSeconds = 23,
            totalSeconds = 30,
            onClick = { onEntryClick("Entry 1") }

        ),
        EntryCardContent(
            imageUrl = "https://proton.me/favicon.ico",
            name = UiText.Dynamic(value = "Proton"),
            label = UiText.Dynamic(value = "proton@email.com"),
            currentCode = UiText.Dynamic(value = "643118"),
            nextCode = UiText.Dynamic(value = "779656"),
            remainingSeconds = 23,
            totalSeconds = 30,
            onClick = { onEntryClick("Entry 2") }
        )
    )

    override val bottomBarContent: Content? = null

}
