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

import kotlinx.datetime.Clock
import kotlin.String

internal class Entry private constructor(
    val id: Int,
    val name: String,
    val issuer: String,
    val uri: String,
    val period: UShort,
    val note: String?,
    val type: EntryType,
    val createdAt: Long,
    val modifiedAt: Long
) {

    internal companion object {

        @Suppress("LongParameterList")
        internal fun create(
            name: String,
            issuer: String,
            uri: String,
            period: UShort,
            note: String?,
            type: Int,
            clock: Clock
        ): Entry = Entry(
            id = 0,
            name = name,
            issuer = issuer,
            uri = uri,
            period = period,
            note = note,
            type = EntryType.from(type),
            createdAt = clock.now().toEpochMilliseconds(),
            modifiedAt = clock.now().toEpochMilliseconds()
        )

        @Suppress("LongParameterList")
        internal fun fromPrimitives(
            id: Int,
            name: String,
            issuer: String,
            uri: String,
            period: UShort,
            note: String?,
            type: Int,
            createdAt: Long,
            modifiedAt: Long
        ): Entry = Entry(
            id = id,
            name = name,
            issuer = issuer,
            uri = uri,
            period = period,
            note = note,
            type = EntryType.from(type),
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )

    }

}
