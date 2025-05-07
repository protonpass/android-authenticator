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

package proton.android.authenticator.features.home.master.presentation

import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.models.UiTextMask

internal data class HomeMasterEntryModel(
    private val entry: Entry,
    private val entryCode: EntryCode,
    private val entryCodesRemainingTimes: Map<Int, Int>,
    private val codeMasks: List<UiTextMask>
) {

    internal val id: String = entry.id

    internal val name: String = entry.name

    internal val nameText: UiText = UiText.Dynamic(value = name)

    internal val issuer: String = entry.issuer

    internal val issuerText: UiText = UiText.Dynamic(value = issuer)

    internal val currentCode: String = entryCode.currentCode

    internal val currentCodeText: UiText = UiText.Dynamic(
        value = currentCode,
        masks = codeMasks
    )

    internal val nextCodeText: UiText = UiText.Dynamic(
        value = entryCode.nextCode,
        masks = codeMasks
    )

    internal val remainingSeconds: Int = entryCodesRemainingTimes.getOrDefault(
        key = entry.period,
        defaultValue = 0
    )

    internal val totalSeconds: Int = entry.period

    internal fun shouldBeShown(query: String): Boolean {
        if (query.isBlank()) {
            return true
        }

        return issuer.contains(query, ignoreCase = true) || name.contains(query, ignoreCase = true)
    }

}
