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

package proton.android.authenticator.business.entries.application.delete

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import org.junit.After
import org.junit.Before
import org.junit.Test
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager

@OptIn(ExperimentalCoroutinesApi::class)
internal class DeleteEntryCommandHandlerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val deleter: EntryDeleter = mockk(relaxed = true)
    private val telemetryManager: AuthenticatorTelemetryManager = mockk(relaxed = true)

    private val handler = DeleteEntryCommandHandler(
        deleter = deleter,
        telemetryManager = telemetryManager
    )

    private val fakeEntry = Entry(
        id = "entry-id",
        content = EncryptedByteArray(byteArrayOf()),
        createdAt = 0L,
        modifiedAt = 0L,
        isDeleted = false,
        isSynced = false,
        position = 0
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
    fun `sends RemoveEntry event on successful deletion`() = runTest {
        coEvery { deleter.delete(any()) } returns fakeEntry

        handler.handle(DeleteEntryCommand(id = "entry-id"))

        coVerify { telemetryManager.sendEvent(AuthenticatorTelemetryEvent.RemoveEntry) }
    }

    @Test
    fun `does not send event when deleter throws exception`() = runTest {
        coEvery { deleter.delete(any()) } throws IllegalStateException("entry not found")

        handler.handle(DeleteEntryCommand(id = "missing-id"))

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }
}
