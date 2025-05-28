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
import proton.android.authenticator.commonrust.AuthenticatorEntryTotpParameters
import proton.android.authenticator.commonrust.IssuerInfo

class Entry private constructor(
    val id: String,
    val name: String,
    val issuer: String,
    val note: String?,
    val period: Int,
    val secret: String,
    val type: EntryType,
    val uri: String,
    val algorithm: EntryAlgorithm,
    val digits: Int,
    val createdAt: Long,
    val modifiedAt: Long,
    val isSynced: Boolean,
    val position: Double,
    val iconUrl: String?
) {

    internal companion object {

        @Suppress("LongParameterList")
        internal fun create(
            model: AuthenticatorEntryModel,
            params: AuthenticatorEntryTotpParameters,
            createdAt: Long,
            modifiedAt: Long,
            isSynced: Boolean,
            position: Double,
            issuerInfo: IssuerInfo? = null
        ): Entry = Entry(
            id = model.id,
            name = model.name,
            issuer = model.issuer,
            note = model.note,
            period = model.period.toInt(),
            secret = model.secret,
            type = EntryType.from(value = model.entryType.ordinal),
            uri = model.uri,
            algorithm = EntryAlgorithm.from(value = params.algorithm.ordinal),
            digits = params.digits.toInt(),
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            isSynced = isSynced,
            position = position,
            iconUrl = issuerInfo?.iconUrl
        )

    }

}
