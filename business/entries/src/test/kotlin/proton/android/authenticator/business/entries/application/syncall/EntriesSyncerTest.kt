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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import proton.android.authenticator.business.entries.domain.EntriesApi
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.EntryRemote
import proton.android.authenticator.business.entries.domain.FetchEntriesResult
import proton.android.authenticator.commonrust.EntryOperation
import proton.android.authenticator.commonrust.OperationType
import proton.android.authenticator.commonrust.SyncOperationCheckerInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContext
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey

@OptIn(ExperimentalCoroutinesApi::class)
internal class EntriesSyncerTest {

    private val testDispatcher = StandardTestDispatcher()

    private val api: EntriesApi = mockk(relaxed = true)
    private val authenticatorClient: proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface =
        mockk(relaxed = true)
    private val encryptionContextProvider: EncryptionContextProvider = mockk()
    private val repository: EntriesRepository = mockk(relaxed = true)
    private val syncOperationChecker: SyncOperationCheckerInterface = mockk()
    private val appDispatchers: AppDispatchers = mockk {
        every { default } returns testDispatcher
        every { io } returns testDispatcher
        every { main } returns testDispatcher
    }

    private val syncer = EntriesSyncer(
        api = api,
        appDispatchers = appDispatchers,
        authenticatorClient = authenticatorClient,
        encryptionContextProvider = encryptionContextProvider,
        repository = repository,
        syncOperationChecker = syncOperationChecker
    )

    // Real key material so decryptSyncKeys succeeds without needing a full crypto stack
    private val masterKey = EncryptionKey(ByteArray(32) { it.toByte() })
    private val encryptedSyncKey = EncryptionContext(masterKey).encrypt(ByteArray(32) { (it + 1).toByte() })
    private val fakeSyncKey = SyncKey(id = "key-id", encryptedKey = encryptedSyncKey)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Execute withEncryptionContext blocks using a real context backed by masterKey
        coEvery {
            encryptionContextProvider.withEncryptionContext<Any>(any(), any())
        } coAnswers {
            val block = secondArg<suspend EncryptionContext.() -> Any>()
            EncryptionContext(masterKey).block()
        }

        coEvery { repository.findAll() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sync completes when entry to delete does not exist locally`() = runTest {
        val foreignLocalId = "foreign-device-uuid"

        val remoteEntry = mockk<EntryRemote>(relaxed = true) {
            every { id } returns "server-entry-id"
            every { localId } returns foreignLocalId
        }
        coEvery { api.fetchAll(any(), any()) } returns FetchEntriesResult(listOf(remoteEntry))

        val deleteOperation = mockk<EntryOperation>(relaxed = true) {
            every { operation } returns OperationType.DELETE_LOCAL
            every { entry } returns mockk(relaxed = true) { every { id } returns foreignLocalId }
        }
        every { syncOperationChecker.calculateOperations(any(), any()) } returns listOf(deleteOperation)

        // Entry does not exist locally — Room throws IllegalStateException for non-null Flow<T>
        coEvery { repository.find(foreignLocalId) } returns flow {
            throw IllegalStateException(
                "The query result was empty," +
                    " but expected a single row to return a NON-NULL object"
            )
        }

        // Should complete without throwing
        syncer.sync(userId = "user-id", keys = listOf(fakeSyncKey), entries = emptyList())

        // Entry was not found locally so there is nothing to remove
        coVerify(exactly = 0) { repository.removeAll(any()) }
    }

    @Test
    fun `sync completes when entry to delete does not exist locally and Room throws NullPointerException`() = runTest {
        val foreignLocalId = "foreign-device-uuid"

        val remoteEntry = mockk<EntryRemote>(relaxed = true) {
            every { id } returns "server-entry-id"
            every { localId } returns foreignLocalId
        }
        coEvery { api.fetchAll(any(), any()) } returns
            FetchEntriesResult(listOf(remoteEntry))

        val deleteOperation = mockk<EntryOperation>(relaxed = true) {
            every { operation } returns OperationType.DELETE_LOCAL
            every { entry } returns mockk(relaxed = true) { every { id } returns foreignLocalId }
        }
        every { syncOperationChecker.calculateOperations(any(), any()) } returns
            listOf(deleteOperation)

        coEvery { repository.find(foreignLocalId) } returns flow {
            throw NullPointerException()
        }

        syncer.sync(userId = "user-id", keys = listOf(fakeSyncKey), entries = emptyList())

        coVerify(exactly = 0) { repository.removeAll(any()) }
    }
}
