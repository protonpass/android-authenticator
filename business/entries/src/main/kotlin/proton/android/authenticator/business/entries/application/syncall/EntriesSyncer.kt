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

import kotlinx.coroutines.CancellationException
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
import proton.android.authenticator.business.entries.domain.EntrySort
import proton.android.authenticator.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.EntryOperation
import proton.android.authenticator.commonrust.OperationType
import proton.android.authenticator.commonrust.SyncOperationCheckerInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
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
        keys: List<SyncKey>,
        entries: List<SyncEntry>
    ): Int {
        val decryptedSyncKeys = decryptSyncKeys(
            encryptionContextProvider = encryptionContextProvider,
            keys = keys
        ) { keyId, error ->
            AuthenticatorLogger.w(TAG, "Could not decrypt sync key: $keyId")
            AuthenticatorLogger.w(TAG, error)
        }
        if (decryptedSyncKeys.isEmpty()) throw NoValidSyncKeysException()

        val (primaryKey, primaryEncryptionKey) = decryptedSyncKeys.first()
        val encryptionKeys = decryptedSyncKeys.map { (_, encryptionKey) -> encryptionKey }

        val (remoteEntriesMap, undecryptableCount) = getRemoteEntriesMap(userId, encryptionKeys)

        val localEntriesMap = getLocalEntriesMap(entries)

        executeSyncSorting(
            userId = userId,
            remoteEntriesMap = remoteEntriesMap,
            localEntriesMap = localEntriesMap
        )

        executeSyncOperations(
            userId = userId,
            keyId = primaryKey.id,
            encryptionKey = primaryEncryptionKey,
            remoteEntriesMap = remoteEntriesMap,
            localEntriesMap = localEntriesMap
        )

        return undecryptableCount
    }

    private fun getLocalEntriesMap(syncEntries: List<SyncEntry>) = syncEntries
        .map(::EntryLocal)
        .associateBy(EntryLocal::id)

    private suspend fun getRemoteEntriesMap(
        userId: String,
        encryptionKeys: List<EncryptionKey>
    ): Pair<Map<String, EntryRemote>, Int> {
        val result = api.fetchAll(
            userId = userId,
            encryptionKeys = encryptionKeys
        )
        return result.entries.associateBy(EntryRemote::id) to result.undecryptableCount
    }

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
            ?.let { entryIds ->
                entryIds.chunked(BATCH_SIZE).forEach { batch ->
                    api.deleteAll(userId = userId, entryIds = batch)
                }
            }
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
            ?.let { entryModels ->
                entryModels.chunked(BATCH_SIZE).forEach { batch ->
                    val entriesRemote = api.createAll(
                        userId = userId,
                        keyId = keyId,
                        encryptionKey = encryptionKey,
                        entryModels = batch
                    )
                    updateRemoteDataLocally(entriesRemote = entriesRemote)
                }
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
            ?.let { (remoteEntryIds, entryModels) ->
                val filteredRemoteEntryIds = remoteEntryIds.filterNotNull()
                filteredRemoteEntryIds.zip(entryModels).chunked(BATCH_SIZE).forEach { batch ->
                    val (batchEntryIds, batchEntryModels) = batch.unzip()
                    val entriesRemote = api.updateAll(
                        userId = userId,
                        entryIds = batchEntryIds,
                        keyId = keyId,
                        encryptionKey = encryptionKey,
                        entryModels = batchEntryModels,
                        remoteEntriesMap = remoteEntriesMap
                    )
                    updateRemoteDataLocally(entriesRemote = entriesRemote)
                }
            }
    }

    private suspend fun updateRemoteDataLocally(entriesRemote: List<EntryRemote>) {
        entriesRemote
            .takeIfNotEmpty()
            ?.associateBy(EntryRemote::localId)
            ?.let { entriesRemoteMap ->
                repository.findAll()
                    .first()
                    .mapNotNull { entry ->
                        entriesRemoteMap[entry.id]?.let { entryRemote ->
                            entry.copy(
                                modifiedAt = entryRemote.modifiedAt,
                                isSynced = true
                            )
                        }
                    }
            }
            ?.also { updatedEntries -> repository.saveAll(entries = updatedEntries) }
    }

    private suspend fun upsertAll(entryOperations: List<EntryOperation>, remoteEntriesMap: Map<String, EntryRemote>) {
        coroutineScope {
            entryOperations.map { entryOperation ->
                with(entryOperation) {
                    val nonNullRemoteId = requireNotNull(remoteId)
                    async {
                        withContext(appDispatchers.default) {
                            authenticatorClient.serializeEntry(entry)
                        }.let { modelContent ->
                            encryptionContextProvider.withEncryptionContext {
                                encrypt(modelContent, EncryptionTag.EntryContent)
                            }
                        }.let { encryptedModelContent ->
                            val remoteData = remoteEntriesMap.getValue(nonNullRemoteId)
                            Entry(
                                id = entry.id,
                                content = encryptedModelContent,
                                createdAt = remoteData.createdAt,
                                modifiedAt = remoteData.modifiedAt,
                                isDeleted = false,
                                isSynced = true,
                                position = searchLocalEntry(entry)
                                    ?.position
                                    ?: remoteData.position
                            )
                        }
                    }
                }
            }
                .awaitAll()
                .also { entries -> repository.saveAll(entries) }
        }
    }

    private suspend fun executeSyncSorting(
        userId: String,
        remoteEntriesMap: Map<String, EntryRemote>,
        localEntriesMap: Map<String, EntryLocal>
    ) {
        remoteEntriesMap.mapNotNull { (remoteEntryId, remoteEntry) ->
            localEntriesMap[remoteEntry.localId]?.let { localEntry ->
                when {
                    localEntry.position == remoteEntry.position -> {
                        EntrySort(
                            localId = localEntry.id,
                            remoteId = remoteEntryId,
                            position = localEntry.position,
                            modifiedAt = localEntry.modifiedAt
                        )
                    }

                    localEntry.modifiedAt > remoteEntry.modifiedAt -> {
                        EntrySort(
                            localId = localEntry.id,
                            remoteId = remoteEntryId,
                            position = localEntry.position,
                            modifiedAt = localEntry.modifiedAt
                        )
                    }

                    else -> {
                        EntrySort(
                            localId = localEntry.id,
                            remoteId = remoteEntryId,
                            position = remoteEntry.position,
                            modifiedAt = remoteEntry.modifiedAt
                        )
                    }
                }
            }
        }
            .sortedBy(EntrySort::position)
            .let { entriesSort -> entriesSort to sortEntriesRemotely(userId, entriesSort) }
            .also { (entriesSort, modifyTime) -> sortEntriesLocally(entriesSort, modifyTime) }
    }

    private suspend fun sortEntriesRemotely(userId: String, entriesSort: List<EntrySort>): Long? = entriesSort
        .takeIfNotEmpty()
        ?.map(EntrySort::remoteId)
        ?.let { entryIds ->
            api.sortAll(
                userId = userId,
                startingPosition = entriesSort.first().position,
                entryIds = entryIds
            )
        }

    private suspend fun sortEntriesLocally(entriesSort: List<EntrySort>, modifyTime: Long?) {
        entriesSort
            .takeIfNotEmpty()
            ?.associateBy(EntrySort::localId)
            ?.let { entriesSortMap ->
                repository.findAll()
                    .first()
                    .mapNotNull { entry ->
                        entriesSortMap[entry.id]?.let { entrySort ->
                            entry.copy(
                                position = entrySort.position,
                                modifiedAt = modifyTime ?: entrySort.modifiedAt,
                                isSynced = true
                            )
                        }
                    }
            }
            ?.also { sortedEntries -> repository.saveAll(entries = sortedEntries) }
    }

    private suspend fun searchLocalEntry(entryModel: AuthenticatorEntryModel) = try {
        repository.find(entryModel.id).first()
    } catch (_: IllegalStateException) {
        null
    } catch (_: NullPointerException) {
        null
    }

    private companion object {

        private const val BATCH_SIZE = 100
        private const val TAG = "EntriesSyncer"

    }

}

private suspend fun decryptSyncKeys(
    encryptionContextProvider: EncryptionContextProvider,
    keys: List<SyncKey>,
    onKeyDecryptFailure: (keyId: String, error: Throwable) -> Unit
): List<Pair<SyncKey, EncryptionKey>> = encryptionContextProvider.withEncryptionContext {
    keys.mapNotNull { key ->
        runCatching {
            key to EncryptionKey(decrypt(key.encryptedKey))
        }.onFailure { error ->
            if (error is CancellationException) throw error
            onKeyDecryptFailure(key.id, error)
        }.getOrNull()
    }
}

internal class NoValidSyncKeysException : IllegalStateException("No valid sync keys found")
