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
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntryRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.retrofit.RetrofitEntriesDataSource
import proton.android.authenticator.commonrust.AuthenticatorCryptoInterface
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.RemoteEntry
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
internal class EntriesApiImpl @Inject constructor(
    private val apiProvider: ApiProvider,
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val authenticatorCrypto: AuthenticatorCryptoInterface,
    private val encryptionContextProvider: EncryptionContextProvider
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

    override suspend fun fetchAll(userId: String, encryptionKey: EncryptionKey): List<RemoteEntry> {
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
                entriesDto.zip(entryModels) { entryDto, entryModel ->
                    RemoteEntry(
                        remoteId = entryDto.entryId,
                        entry = entryModel,
                        modifyTime = entryDto.modifyTime
                    )
                }
            }
    }

//    override suspend fun fetchAll(
//        userId: String,
//        encryptionKey: EncryptionKey
//    ): List<Entry> = apiProvider
//        .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
//        .invoke { getEntries() }
//        .valueOrThrow
//        .fetchEntriesDto
//        .entries
//        .let { entryDtos ->
//            entryDtos to withContext(appDispatchers.default) {
//                entryDtos
//                    .map { entryDto ->
//                        entryDto.content.let(Base64::decode)
//                    }
//                    .let { ciphertexts ->
//                        authenticatorCrypto.decryptManyEntries(
//                            key = encryptionKey.asByteArray(),
//                            ciphertexts = ciphertexts
//                        )
//                    }
//                    .let(authenticatorClient::serializeEntries)
//            }
//        }
//        .let { (entryDtos, modelContents) ->
//            entryDtos to encryptionContextProvider.withEncryptionContext {
//                modelContents.map { modelContent ->
//                    encrypt(modelContent, EncryptionTag.EntryContent)
//                }
//            }
//        }
//        .let { (entryDtos, encryptedModelContents) ->
//            entryDtos.zip(encryptedModelContents) { entryDto, encryptedModelContent ->
//                Entry(
//                    id = entryDto.entryId,
//                    content = encryptedModelContent,
//                    createdAt = entryDto.createTime,
//                    modifiedAt = entryDto.modifyTime,
//                    isSynced = false,
//                    position = 0.0,
//                    iconUrl = null,
//                )
//            }
//        }

}
