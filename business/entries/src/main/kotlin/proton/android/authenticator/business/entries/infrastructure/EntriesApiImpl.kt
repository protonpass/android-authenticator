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

package proton.android.authenticator.business.entries.infrastructure

import kotlinx.coroutines.withContext
import me.proton.core.domain.entity.UserId
import me.proton.core.network.data.ApiProvider
import proton.android.authenticator.business.entries.domain.EntriesApi
import proton.android.authenticator.business.entries.domain.EntryRemote
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntryRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.DeleteEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntryRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.retrofit.RetrofitEntriesDataSource
import proton.android.authenticator.commonrust.AuthenticatorCryptoInterface
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
internal class EntriesApiImpl @Inject constructor(
    private val apiProvider: ApiProvider,
    private val appDispatchers: AppDispatchers,
    private val authenticatorCrypto: AuthenticatorCryptoInterface
) : EntriesApi() {

    override suspend fun create(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModel: AuthenticatorEntryModel
    ) {
        withContext(appDispatchers.default) {
            authenticatorCrypto.encryptEntry(
                key = encryptionKey.asByteArray(),
                model = entryModel
            ).let { encryptedEntryModel ->
                CreateEntryRequestDto(
                    authenticatorKeyID = keyId,
                    content = Base64.encodeToByteArray(encryptedEntryModel).let(::String),
                    contentFormatVersion = contentFormatVersion
                )
            }
        }
            .also { request ->
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { createEntry(request = request) }
                    .valueOrThrow
            }
    }

    override suspend fun createAll(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModels: List<AuthenticatorEntryModel>
    ) {
        withContext(appDispatchers.default) {
            authenticatorCrypto.encryptManyEntries(
                key = encryptionKey.asByteArray(),
                models = entryModels
            ).map { encryptedEntryModel ->
                CreateEntryRequestDto(
                    authenticatorKeyID = keyId,
                    content = Base64.encodeToByteArray(encryptedEntryModel).let(::String),
                    contentFormatVersion = contentFormatVersion
                )
            }
        }
            .let(::CreateEntriesRequestDto)
            .also { request ->
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { createEntries(request = request) }
                    .valueOrThrow
            }
    }

    override suspend fun delete(userId: String, entryId: String) {
        apiProvider
            .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
            .invoke { deleteEntry(entryId = entryId) }
            .valueOrThrow
    }

    override suspend fun deleteAll(userId: String, entryIds: List<String>) {
        entryIds
            .let(::DeleteEntriesRequestDto)
            .also { request ->
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { deleteEntries(request = request) }
                    .valueOrThrow
            }
    }

    override suspend fun fetchAll(userId: String, encryptionKey: EncryptionKey): List<EntryRemote> {
        var lastId: String? = null

        return buildList {
            do {
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { getEntries(lastId = lastId) }
                    .valueOrThrow
                    .fetchEntriesDto
                    .also { fetchEntriesDto -> lastId = fetchEntriesDto.lastId }
                    .also { fetchEntriesDto -> addAll(fetchEntriesDto.entries) }
            } while (lastId != null)
        }
            .let { entriesDto ->
                entriesDto to withContext(appDispatchers.default) {
                    entriesDto
                        .map { entryDto ->
                            entryDto.content.let(Base64::decode)
                        }
                        .let { ciphertexts ->
                            authenticatorCrypto.decryptManyEntries(
                                key = encryptionKey.asByteArray(),
                                ciphertexts = ciphertexts
                            )
                        }
                }
            }
            .let { (entriesDto, entryModels) ->
                entriesDto.withIndex().zip(entryModels) { indexedEntryDto, entryModel ->
                    EntryRemote(
                        id = indexedEntryDto.value.entryId,
                        revision = indexedEntryDto.value.revision,
                        modifiedAt = indexedEntryDto.value.modifyTime,
                        position = indexedEntryDto.index,
                        model = entryModel
                    )
                }
            }
    }

    override suspend fun update(
        userId: String,
        entryId: String,
        entryRevision: Int,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModel: AuthenticatorEntryModel
    ) {
        withContext(appDispatchers.default) {
            authenticatorCrypto.encryptEntry(
                key = encryptionKey.asByteArray(),
                model = entryModel
            ).let { encryptedEntryModel ->
                UpdateEntryRequestDto(
                    authenticatorKeyID = keyId,
                    entryId = entryId,
                    content = Base64.encodeToByteArray(encryptedEntryModel).let(::String),
                    contentFormatVersion = contentFormatVersion,
                    lastRevision = entryRevision
                )
            }
        }
            .also { request ->
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { updateEntry(entryId = entryId, request = request) }
                    .valueOrThrow
            }
    }

    override suspend fun updateAll(
        userId: String,
        entryIds: List<String>,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModels: List<AuthenticatorEntryModel>,
        remoteEntriesMap: Map<String, EntryRemote>
    ) {
        withContext(appDispatchers.default) {
            authenticatorCrypto.encryptManyEntries(
                key = encryptionKey.asByteArray(),
                models = entryModels
            )
                .zip(entryIds)
                .map { (encryptedEntryModel, entryId) ->
                    UpdateEntryRequestDto(
                        authenticatorKeyID = keyId,
                        entryId = entryId,
                        content = Base64.encodeToByteArray(encryptedEntryModel).let(::String),
                        contentFormatVersion = contentFormatVersion,
                        lastRevision = remoteEntriesMap.getValue(entryId).revision
                    )
                }
        }
            .let(::UpdateEntriesRequestDto)
            .also { request ->
                apiProvider
                    .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
                    .invoke { updateEntries(request = request) }
                    .valueOrThrow
            }
    }

}
