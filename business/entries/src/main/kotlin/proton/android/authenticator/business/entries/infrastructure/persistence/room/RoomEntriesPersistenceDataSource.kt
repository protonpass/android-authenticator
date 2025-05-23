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

package proton.android.authenticator.business.entries.infrastructure.persistence.room

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntryEntity
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContext
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

internal class RoomEntriesPersistenceDataSource @Inject constructor(
    private val entriesDao: EntriesDao,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val authenticatorClient: AuthenticatorMobileClientInterface
) : PersistenceDataSource<Entry> {

    override fun observeAll(): Flow<List<Entry>> = entriesDao.observeAll()
        .map { entryEntities ->
            coroutineScope {
                encryptionContextProvider.withEncryptionContext {
                    entryEntities.map { entryEntity ->
                        async {
                            entryEntity.toDomain(this@withEncryptionContext, authenticatorClient)
                        }
                    }
                }.awaitAll()
            }
        }

    override fun byId(id: String): Flow<Entry> = entriesDao.observeById(id)
        .map { entryEntity ->
            encryptionContextProvider.withEncryptionContext {
                entryEntity.toDomain(this@withEncryptionContext, authenticatorClient)
            }
        }

    override suspend fun delete(entry: Entry) {
        encryptionContextProvider.withEncryptionContext {
            entry.toEntity(authenticatorClient, this@withEncryptionContext)
                .also { entryEntity -> entriesDao.delete(entryEntity) }
        }
    }

    override suspend fun insert(entry: Entry) {
        encryptionContextProvider.withEncryptionContext {
            entry.toEntity(authenticatorClient, this@withEncryptionContext)
                .also { entryEntity -> entriesDao.upsert(entryEntity) }
        }
    }

    override suspend fun insertAll(entries: List<Entry>) {
        encryptionContextProvider.withEncryptionContext {
            entries.map { entry -> entry.toEntity(authenticatorClient, this@withEncryptionContext) }
                .also { entryEntities -> entriesDao.upsertAll(entryEntities) }
        }
    }

}

private fun Entry.toEntity(
    authenticatorClient: AuthenticatorMobileClientInterface,
    encryptionContext: EncryptionContext
): EntryEntity = AuthenticatorEntryModel(
    id = id,
    name = name,
    issuer = issuer,
    secret = secret,
    uri = uri,
    period = period.toUShort(),
    note = note,
    entryType = type.asAuthenticatorEntryType()
)
    .let { entryModel ->
        authenticatorClient.serializeEntry(entryModel)
    }
    .let { decryptedEntityContent ->
        encryptionContext.encrypt(decryptedEntityContent, EncryptionTag.EntryContent)
    }
    .let { encryptedEntityContent ->
        EntryEntity(
            id = id,
            content = encryptedEntityContent,
            type = type.value,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }

private fun EntryEntity.toDomain(
    encryptionContext: EncryptionContext,
    authenticatorClient: AuthenticatorMobileClientInterface
): Entry = encryptionContext.decrypt(content, EncryptionTag.EntryContent)
    .let { decryptedEntityContent ->
        authenticatorClient.deserializeEntry(decryptedEntityContent)
    }
    .let { entryModel ->
        Entry(
            model = entryModel,
            params = authenticatorClient.getTotpParams(entryModel),
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
