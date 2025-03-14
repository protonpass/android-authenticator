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

package proton.android.authenticator.business.entries.domain

import proton.android.authenticator.commonrust.AuthenticatorEntryModel

data class Entry(
    val id: Int,
    val createdAt: Long,
    val modifiedAt: Long,
    internal val model: AuthenticatorEntryModel
) {

    val issuer: String = model.issuer

    val name: String = model.name

    val note: String? = model.note

    val period: Int = model.period.toInt()

    val secret: String = model.secret

    val type: EntryType = EntryType.from(value = model.entryType.ordinal)

    val uri: String = model.uri

}
