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
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.business.entrycodes.domain.EntryCode

internal data class HomeMasterEntryModel(
    private val entry: Entry,
    private val entryCode: EntryCode
) {

    internal val id: String = entry.id

    internal val name: String = entry.name

    internal val secret: String = entry.secret

    internal val digits: Int = entry.digits

    internal val algorithm: EntryAlgorithm = entry.algorithm

    internal val timeInterval: Int = entry.period

    internal val issuer: String = entry.issuer

    internal val currentCode: String = entryCode.currentCode

    internal val nextCode: String = entryCode.nextCode

    internal val totalSeconds: Int = entry.period

    internal val type: EntryType = entry.type

    internal val position: Double = entry.position

    internal val iconUrl: String = entry.iconUrl.orEmpty()

    internal fun shouldBeShown(query: String): Boolean {
        if (query.isBlank()) {
            return true
        }

        return issuer.contains(query, ignoreCase = true) || name.contains(query, ignoreCase = true)
    }

}
