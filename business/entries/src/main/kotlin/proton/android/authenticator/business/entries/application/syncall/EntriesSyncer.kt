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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.proton.core.util.kotlin.takeIfNotEmpty
import proton.android.authenticator.business.entries.domain.EntriesApi
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryLocal
import proton.android.authenticator.business.entries.domain.EntryRemote
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.EntryOperation
import proton.android.authenticator.commonrust.OperationType
import proton.android.authenticator.commonrust.SyncOperationCheckerInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
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
    private val syncOperationChecker: SyncOperationCheckerInterface
) {

    internal suspend fun sync(
        userId: String,
        key: SyncKey,
        entries: List<SyncEntry>
    ) {
        val encryptionKey = generateEncryptionKey(key)

        val remoteEntriesMap = getRemoteEntriesMap(userId, encryptionKey)

        val localEntriesMap = getLocalEntriesMap(entries)

        executeSyncOperations(
            userId = userId,
            keyId = key.id,
            encryptionKey = encryptionKey,
            remoteEntriesMap = remoteEntriesMap,
            localEntriesMap = localEntriesMap
        )
    }

    private suspend fun generateEncryptionKey(key: SyncKey) = encryptionContextProvider
        .withEncryptionContext { decrypt(key.encryptedKey) }
        .let(::EncryptionKey)

    private fun getLocalEntriesMap(syncEntries: List<SyncEntry>) = syncEntries
        .map(::EntryLocal)
        .associateBy(EntryLocal::id)

    private suspend fun getRemoteEntriesMap(userId: String, encryptionKey: EncryptionKey) = api
        .fetchAll(
            userId = userId,
            encryptionKey = encryptionKey
        )
        .associateBy(EntryRemote::id)

    private suspend fun executeSyncOperations(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        remoteEntriesMap: Map<String, EntryRemote>,
        localEntriesMap: Map<String, EntryLocal>
    ) {
        withContext(appDispatchers.default) {
            syncOperationChecker.calculateOperations(
                remote = remoteEntriesMap.map { it.value.operation },
                local = localEntriesMap.map { it.value.operation }
            ).groupBy { entryOperation -> entryOperation.operation }
        }.let { entryOperationsMap ->
            coroutineScope {
                entryOperationsMap.keys.map { operationType ->
                    when (operationType) {
                        OperationType.DELETE_LOCAL -> {
                            async {
                                deleteAllLocal(
                                    entryOperations = entryOperationsMap.getOperations(operationType)
                                )
                            }
                        }

                        OperationType.DELETE_LOCAL_AND_REMOTE -> {
                            async {
                                deleteAllLocalAndRemote(
                                    entryOperations = entryOperationsMap.getOperations(operationType),
                                    userId = userId
                                )
                            }
                        }

                        OperationType.PUSH -> {
                            async {
                                pushAll(
                                    entryOperations = entryOperationsMap.getOperations(operationType),
                                    userId = userId,
                                    keyId = keyId,
                                    encryptionKey = encryptionKey,
                                    localEntriesMap = localEntriesMap,
                                    remoteEntriesMap = remoteEntriesMap
                                )
                            }
                        }

                        OperationType.UPSERT -> {
                            async {
                                upsertAll(
                                    entryOperations = entryOperationsMap.getOperations(operationType),
                                    remoteEntriesMap = remoteEntriesMap
                                )
                            }
                        }
                    }
                }
            }
        }.awaitAll()
    }

    private fun Map<OperationType, List<EntryOperation>>.getOperations(type: OperationType) = getOrDefault(
        key = type,
        defaultValue = emptyList()
    )

    private suspend fun deleteAllLocalAndRemote(entryOperations: List<EntryOperation>, userId: String) {
        entryOperations
            .mapNotNull { entryOperation -> entryOperation.remoteId }
            .takeIfNotEmpty()
            ?.let { entryIds -> api.deleteAll(userId = userId, entryIds = entryIds) }
            .also { deleteAllLocal(entryOperations = entryOperations) }
    }

    private suspend fun deleteAllLocal(entryOperations: List<EntryOperation>) {
        entryOperations
            .map(EntryOperation::entry)
            .mapNotNull { entryModel -> searchLocalEntry(entryModel) }
            .takeIfNotEmpty()
            ?.also { entries -> repository.removeAll(entries) }
    }

    @Suppress("LongParameterList")
    private suspend fun pushAll(
        entryOperations: List<EntryOperation>,
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        localEntriesMap: Map<String, EntryLocal>,
        remoteEntriesMap: Map<String, EntryRemote>
    ) {
        entryOperations
            .map { entryOperation -> entryOperation.remoteId to entryOperation.entry }
            .partition { (remoteEntryId, _) -> remoteEntryId == null }
            .also { (createEntryIdsAndEntryModels, updateEntryIdsAndEntryModels) ->
                coroutineScope {
                    launch {
                        createAll(
                            userId = userId,
                            keyId = keyId,
                            encryptionKey = encryptionKey,
                            remoteEntryIdsAndEntryModels = createEntryIdsAndEntryModels,
                            localEntriesMap = localEntriesMap
                        )
                    }

                    launch {
                        updateAll(
                            userId = userId,
                            keyId = keyId,
                            encryptionKey = encryptionKey,
                            remoteEntryIdsAndEntryModels = updateEntryIdsAndEntryModels,
                            remoteEntriesMap = remoteEntriesMap
                        )
                    }
                }
            }
    }

    private suspend fun createAll(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        remoteEntryIdsAndEntryModels: List<Pair<String?, AuthenticatorEntryModel>>,
        localEntriesMap: Map<String, EntryLocal>
    ) {
        remoteEntryIdsAndEntryModels
            .mapNotNull { (_, entryModel) -> localEntriesMap[entryModel.id] }
            .takeIfNotEmpty()
            ?.sortedBy(EntryLocal::position)
            ?.map(EntryLocal::model)
            ?.also { entryModels ->
                api.createAll(
                    userId = userId,
                    keyId = keyId,
                    encryptionKey = encryptionKey,
                    entryModels = entryModels
                )
            }
    }

    private suspend fun updateAll(
        userId: String,
        keyId: String,
        encryptionKey: EncryptionKey,
        remoteEntryIdsAndEntryModels: List<Pair<String?, AuthenticatorEntryModel>>,
        remoteEntriesMap: Map<String, EntryRemote>
    ) {
        remoteEntryIdsAndEntryModels
            .takeIfNotEmpty()
            ?.unzip()
            ?.also { (remoteEntryIds, entryModels) ->
                api.updateAll(
                    userId = userId,
                    entryIds = remoteEntryIds.filterNotNull(),
                    keyId = keyId,
                    encryptionKey = encryptionKey,
                    entryModels = entryModels,
                    remoteEntriesMap = remoteEntriesMap
                )
            }
    }

    private suspend fun upsertAll(entryOperations: List<EntryOperation>, remoteEntriesMap: Map<String, EntryRemote>) {
        coroutineScope {
            entryOperations.map { entryOperation ->
                with(entryOperation) {
                    async {
                        withContext(appDispatchers.default) {
                            authenticatorClient.serializeEntry(entry)
                        }.let { modelContent ->
                            encryptionContextProvider.withEncryptionContext {
                                encrypt(modelContent, EncryptionTag.EntryContent)
                            }
                        }.let { encryptedModelContent ->
                            Entry(
                                id = entry.id,
                                content = encryptedModelContent,
                                modifiedAt = remoteEntriesMap.getValue(remoteId!!).modifiedAt,
                                isDeleted = false,
                                isSynced = true,
                                position = searchLocalEntry(entry)
                                    ?.position
                                    ?: repository.searchMaxPosition()
                            )
                        }
                    }
                }
            }
                .awaitAll()
                .also { entries -> repository.saveAll(entries) }
        }
    }

    private suspend fun searchLocalEntry(entryModel: AuthenticatorEntryModel) = try {
        repository.find(entryModel.id).first()
    } catch (_: NullPointerException) {
        null
    }

}
