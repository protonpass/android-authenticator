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

package proton.android.authenticator.business.entries.application.syncall

import me.proton.core.crypto.common.keystore.EncryptedByteArray
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntryType
import proton.android.authenticator.commonrust.LocalEntryState
import proton.android.authenticator.shared.common.domain.infrastructure.commands.Command

data class SyncEntriesCommand(
    internal val userId: String,
    internal val key: SyncKey,
    internal val entries: List<SyncEntry>
) : Command

data class SyncEntry(
    internal val id: String,
    internal val position: Int,
    internal val modifyTime: Long,
    private val name: String,
    private val issuer: String,
    private val note: String?,
    private val period: Int,
    private val secret: String,
    private val type: EntryType,
    private val uri: String,
    private val isDeleted: Boolean,
    private val isSynced: Boolean
) {

    internal val model: AuthenticatorEntryModel = AuthenticatorEntryModel(
        id = id,
        name = name,
        issuer = issuer,
        secret = secret,
        uri = uri,
        period = period.toUShort(),
        note = note,
        entryType = enumValues<AuthenticatorEntryType>()[type.ordinal]
    )

    internal val state: LocalEntryState = when {
        isDeleted -> LocalEntryState.PENDING_TO_DELETE
        isSynced -> LocalEntryState.SYNCED
        else -> LocalEntryState.PENDING_SYNC
    }

}

data class SyncKey(
    internal val id: String,
    internal val encryptedKey: EncryptedByteArray
)
