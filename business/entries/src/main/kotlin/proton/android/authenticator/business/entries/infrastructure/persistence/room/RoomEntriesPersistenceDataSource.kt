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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntryEntity
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntryType
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
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
            encryptionContextProvider.withEncryptionContext {
                entryEntities.map { entryEntity ->
                    decrypt(entryEntity.content, EncryptionTag.EntryContent)
                        .let { decryptedEntityContent ->
                            authenticatorClient.deserializeEntries(listOf(decryptedEntityContent))
                                .first()
                        }
                        .let { entryModel ->
                            Entry.fromPrimitives(
                                id = entryEntity.id,
                                name = entryModel.name,
                                uri = entryModel.uri,
                                period = entryModel.period,
                                note = entryModel.note,
                                type = entryModel.entryType.ordinal,
                                createdAt = entryEntity.createdAt,
                                modifiedAt = entryEntity.modifiedAt
                            )
                        }
                }
            }
        }

    override suspend fun insert(entry: Entry) {
        AuthenticatorEntryModel(
            name = entry.name,
            uri = entry.uri,
            period = entry.period,
            note = entry.note,
            entryType = AuthenticatorEntryType.TOTP
        )
            .let { entryModel ->
                authenticatorClient.serializeEntries(listOf(entryModel)).first()
            }
            .let { decryptedEntityContent ->
                encryptionContextProvider.withEncryptionContext {
                    encrypt(decryptedEntityContent, EncryptionTag.EntryContent)
                }
            }
            .let { encryptedEntityContent ->
                EntryEntity(
                    content = encryptedEntityContent,
                    type = entry.type.value,
                    createdAt = entry.createdAt,
                    modifiedAt = entry.modifiedAt
                )
            }
            .also { entryEntity ->
                entriesDao.upsert(entryEntity)
            }
    }

}
