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

import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.features.shared.entries.presentation.EntryModel

internal data class HomeMasterEntryModel(
    internal val entryModel: EntryModel,
    private val entryCode: EntryCode
) {

    internal val id: String = entryModel.id

    internal val name: String = entryModel.name

    internal val secret: String = entryModel.secret

    internal val digits: Int = entryModel.digits

    internal val algorithm: EntryAlgorithm = entryModel.algorithm

    internal val timeInterval: Int = entryModel.period

    internal val issuer: String = entryModel.issuer

    internal val currentCode: String = entryCode.currentCode

    internal val nextCode: String = entryCode.nextCode

    internal val totalSeconds: Int = entryModel.period

    internal val type: EntryType = entryModel.type

    internal val position: Int = entryModel.position

    internal val iconUrl: String = entryModel.iconUrl.orEmpty()

}
