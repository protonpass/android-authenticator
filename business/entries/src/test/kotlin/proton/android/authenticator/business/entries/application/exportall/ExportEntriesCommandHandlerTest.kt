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

package proton.android.authenticator.business.entries.application.exportall

import android.net.Uri
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExportEntriesCommandHandlerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val entriesExporter: EntriesExporter = mockk(relaxed = true)
    private val telemetryManager: AuthenticatorTelemetryManager = mockk(relaxed = true)

    private val handler = ExportEntriesCommandHandler(
        entriesExporter = entriesExporter,
        telemetryManager = telemetryManager
    )

    private val command = ExportEntriesCommand(
        destinationUri = mockk<Uri>(relaxed = true),
        password = null
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
    fun `sends Export event on successful export`() = runTest {
        coEvery { entriesExporter.export(any(), any()) } returns 10

        handler.handle(command)

        coVerify { telemetryManager.sendEvent(AuthenticatorTelemetryEvent.Export) }
    }

    @Test
    fun `does not send event when exporter throws exception`() = runTest {
        coEvery { entriesExporter.export(any(), any()) } throws IOException("export failed")

        handler.handle(command)

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }
}
