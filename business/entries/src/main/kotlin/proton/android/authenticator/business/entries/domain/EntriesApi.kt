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
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey

internal abstract class EntriesApi {

    protected val contentFormatVersion = CONTENT_FORMAT_VERSION

    internal abstract suspend fun create(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModel: AuthenticatorEntryModel
    )

    internal abstract suspend fun createAll(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModels: List<AuthenticatorEntryModel>
    )

    internal abstract suspend fun delete(userId: String, entryId: String)

    internal abstract suspend fun fetchAll(userId: String, encryptionKey: EncryptionKey): List<EntryRemote>

    @Suppress("LongParameterList")
    internal abstract suspend fun update(
        userId: String,
        entryId: String,
        entryRevision: Int,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModel: AuthenticatorEntryModel
    )

    private companion object {

        private const val CONTENT_FORMAT_VERSION = 1

    }

}
