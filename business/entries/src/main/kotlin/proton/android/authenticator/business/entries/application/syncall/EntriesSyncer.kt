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

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.entries.domain.EntriesApi
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.LocalEntry
import proton.android.authenticator.commonrust.OperationType
import proton.android.authenticator.commonrust.RemoteEntry
import proton.android.authenticator.commonrust.SyncOperationCheckerInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

internal class EntriesSyncer @Inject constructor(
    private val api: EntriesApi,
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val repository: EntriesRepository,
    private val syncOperationChecker: SyncOperationCheckerInterface,
    private val timeProvider: TimeProvider
) {

    internal suspend fun sync(
        userId: String,
        key: SyncKey,
        entries: List<SyncEntry>
    ) {
        val encryptionKey = generateEncryptionKey(key)

        val remoteEntries = getRemoteEntries(userId, encryptionKey)

        val localEntries = getLocalEntries(entries)

        executeSyncOperations(
            userId = userId,
            keyId = key.id,
            encryptionKey = encryptionKey,
            remoteEntries = remoteEntries,
            localEntries = localEntries
        )

//
//        api.createAll(
//            userId = userId,
//            keyId = key.id,
//            encryptionKey = encryptionKey,
//            entryModels = entries.map(SyncEntry::toModel),
//        ).also {
//            println("JIBIRI: created entries")
//        }
    }

    private suspend fun generateEncryptionKey(key: SyncKey) = encryptionContextProvider
        .withEncryptionContext { decrypt(key.encryptedKey) }
        .let(::EncryptionKey)

    private fun getLocalEntries(syncEntries: List<SyncEntry>) = syncEntries.map { syncEntry ->
        LocalEntry(
            entry = syncEntry.model,
            state = syncEntry.state,
            modifyTime = syncEntry.modifyTime,
            localModifyTime = null
        )
    }

    private suspend fun getRemoteEntries(userId: String, encryptionKey: EncryptionKey) = api
        .fetchAll(
            userId = userId,
            encryptionKey = encryptionKey
        )

    private suspend fun executeSyncOperations(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        remoteEntries: List<RemoteEntry>,
        localEntries: List<LocalEntry>
    ) {
        coroutineScope {
            println("JIBIRI: executing sync operations")
            println("JIBIRI: remote entries: ${remoteEntries.size}")
            println("JIBIRI: local entries: ${localEntries.size}")

            syncOperationChecker.calculateOperations(remoteEntries, localEntries)
                .map { entryOperation ->
                    println("JIBIRI: operation: ${entryOperation.operation}")
                    with(entryOperation) {
                        when (operation) {
                            OperationType.DELETE_LOCAL -> async {
                                deleteLocal(entryModel = entry)
                            }

                            OperationType.DELETE_LOCAL_AND_REMOTE -> async {
                                deleteLocalAndRemote(userId = userId, entryModel = entry)
                            }

                            OperationType.PUSH -> async {
                                push(
                                    userId = userId,
                                    keyId = keyId,
                                    encryptionKey = encryptionKey,
                                    entryModel = entry
                                )
                            }

                            OperationType.UPSERT -> async {
                                upsert(entryModel = entry)
                            }
                        }
                    }
                }
        }.awaitAll()
    }

    private suspend fun deleteLocal(entryModel: AuthenticatorEntryModel) {
        searchLocalEntry(entryModel)?.also { entry -> repository.remove(entry) }
    }

    private suspend fun deleteLocalAndRemote(userId: String, entryModel: AuthenticatorEntryModel) {
        api.delete(
            userId = userId,
            entryId = entryModel.id
        ).also {
            deleteLocal(entryModel = entryModel)
        }
    }

    private suspend fun push(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        entryModel: AuthenticatorEntryModel
    ) {
        api.create(
            userId = userId,
            keyId = keyId,
            encryptionKey = encryptionKey,
            entryModel = entryModel
        )
    }

    private suspend fun upsert(entryModel: AuthenticatorEntryModel) {
        val localEntry = searchLocalEntry(entryModel)

        withContext(appDispatchers.default) {
            authenticatorClient.serializeEntry(entryModel)
        }
            .let { modelContent ->
                encryptionContextProvider.withEncryptionContext {
                    encrypt(modelContent, EncryptionTag.EntryContent)
                }
            }
            .let { encryptedModelContent ->
                Entry(
                    id = entryModel.id,
                    content = encryptedModelContent,
                    modifiedAt = timeProvider.currentMillis(),
                    isDeleted = false,
                    isSynced = true,
                    position = localEntry?.position ?: repository.searchMaxPosition()
                )
            }
            .also { entry ->
                repository.save(entry)
            }

    }

    private suspend fun searchLocalEntry(entryModel: AuthenticatorEntryModel) = try {
        repository.find(entryModel.id).first()
    } catch (_: NullPointerException) {
        null
    }

}
