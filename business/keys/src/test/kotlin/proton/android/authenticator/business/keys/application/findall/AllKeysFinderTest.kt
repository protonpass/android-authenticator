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

package proton.android.authenticator.business.keys.application.findall

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import me.proton.core.user.domain.entity.User
import me.proton.core.user.domain.entity.UserKey
import me.proton.core.user.domain.repository.UserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import proton.android.authenticator.business.keys.application.create.KeyCreator
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.keys.domain.KeysApi
import proton.android.authenticator.business.keys.domain.KeysRepository

@OptIn(ExperimentalCoroutinesApi::class)
internal class AllKeysFinderTest {

    private val testDispatcher = StandardTestDispatcher()

    private val api: KeysApi = mockk(relaxed = true)
    private val keyCreator: KeyCreator = mockk(relaxed = true)
    private val repository: KeysRepository = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    private val finder = AllKeysFinder(
        api = api,
        keyCreator = keyCreator,
        repository = repository,
        userRepository = userRepository
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `keys are sorted so the one matching the primary user key comes first`() = runTest {
        val primaryUserKeyId = "primary-user-key-id"
        val oldUserKeyId = "old-user-key-id"

        val oldKey = makeKey(id = "old-key", userKeyId = oldUserKeyId)
        val pqKey = makeKey(id = "pq-key", userKeyId = primaryUserKeyId)

        coEvery { repository.findAll() } returns flowOf(listOf(oldKey, pqKey))
        coEvery { api.fetchAll(any()) } returns listOf(oldKey, pqKey)
        coEvery { userRepository.getUser(any()) } returns makeUser(
            primaryUserKeyId = primaryUserKeyId,
            secondaryUserKeyId = oldUserKeyId
        )

        val result = finder.findAll(userId = "user-id", forceRefresh = false).first()

        assertEquals("pq-key", result.first().id)
        assertEquals("old-key", result.last().id)
    }

    @Test
    fun `keys are returned unsorted when user repository fails`() = runTest {
        val oldKey = makeKey(id = "old-key", userKeyId = "old-user-key-id")
        val pqKey = makeKey(id = "pq-key", userKeyId = "pq-user-key-id")

        coEvery { repository.findAll() } returns flowOf(listOf(oldKey, pqKey))
        coEvery { api.fetchAll(any()) } returns listOf(oldKey, pqKey)
        coEvery { userRepository.getUser(any()) } throws RuntimeException("network error")

        val result = finder.findAll(userId = "user-id", forceRefresh = false).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `migration is triggered when no remote key uses the primary user key`() = runTest {
        val primaryUserKeyId = "primary-user-key-id"
        val oldUserKeyId = "old-user-key-id"

        val oldKey = makeKey(id = "old-key", userKeyId = oldUserKeyId)

        coEvery { repository.findAll() } returns flowOf(listOf(oldKey))
        coEvery { api.fetchAll(any()) } returns listOf(oldKey)
        coEvery { userRepository.getUser(any()) } returns makeUser(
            primaryUserKeyId = primaryUserKeyId,
            secondaryUserKeyId = oldUserKeyId
        )

        finder.findAll(userId = "user-id", forceRefresh = true).first()

        coVerify(exactly = 1) { keyCreator.create(userId = "user-id") }
    }

    @Test
    fun `migration is not triggered when a remote key already uses the primary user key`() = runTest {
        val primaryUserKeyId = "primary-user-key-id"

        val pqKey = makeKey(id = "pq-key", userKeyId = primaryUserKeyId)

        coEvery { repository.findAll() } returns flowOf(listOf(pqKey))
        coEvery { api.fetchAll(any()) } returns listOf(pqKey)
        coEvery { userRepository.getUser(any()) } returns makeUser(
            primaryUserKeyId = primaryUserKeyId,
            secondaryUserKeyId = null
        )

        finder.findAll(userId = "user-id", forceRefresh = true).first()

        coVerify(exactly = 0) { keyCreator.create(any()) }
    }

    @Test
    fun `migration is not triggered when user repository fails during sync`() = runTest {
        val oldKey = makeKey(id = "old-key", userKeyId = "old-user-key-id")

        coEvery { repository.findAll() } returns flowOf(listOf(oldKey))
        coEvery { api.fetchAll(any()) } returns listOf(oldKey)
        coEvery { userRepository.getUser(any()) } throws RuntimeException("network error")

        finder.findAll(userId = "user-id", forceRefresh = true).first()

        coVerify(exactly = 0) { keyCreator.create(any()) }
    }

    private fun makeKey(id: String, userKeyId: String) = Key(
        id = id,
        encryptedKey = EncryptedByteArray(ByteArray(0)),
        key = "",
        userId = "user-id",
        userKeyId = userKeyId
    )

    private fun makeUser(primaryUserKeyId: String, secondaryUserKeyId: String?): User {
        val userKeys = buildList {
            add(makeUserKey(keyId = primaryUserKeyId, isPrimary = true))
            if (secondaryUserKeyId != null) {
                add(makeUserKey(keyId = secondaryUserKeyId, isPrimary = false))
            }
        }
        return mockk<User> { every { keys } returns userKeys }
    }

    private fun makeUserKey(keyId: String, isPrimary: Boolean): UserKey = mockk {
        every { this@mockk.keyId } returns mockk { every { id } returns keyId }
        every { privateKey } returns mockk { every { this@mockk.isPrimary } returns isPrimary }
    }
}
