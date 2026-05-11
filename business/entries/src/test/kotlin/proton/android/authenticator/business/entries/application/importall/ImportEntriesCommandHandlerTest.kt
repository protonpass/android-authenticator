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

package proton.android.authenticator.business.entries.application.importall

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
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class ImportEntriesCommandHandlerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val importer: EntriesImporter = mockk(relaxed = true)
    private val telemetryManager: AuthenticatorTelemetryManager = mockk(relaxed = true)

    private val handler = ImportEntriesCommandHandler(
        importer = importer,
        telemetryManager = telemetryManager
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun commandFor(importType: EntryImportType): ImportEntriesCommand.FromBytes =
        ImportEntriesCommand.FromBytes(importType = importType, contentBytes = emptyList())

    @Test
    fun `sends Import with Aegis source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Aegis) } returns 5

        handler.handle(commandFor(EntryImportType.Aegis))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.Aegis,
                    entriesCount = 5
                )
            )
        }
    }

    @Test
    fun `sends Import with Bitwarden source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Bitwarden) } returns 3

        handler.handle(commandFor(EntryImportType.Bitwarden))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.Bitwarden,
                    entriesCount = 3
                )
            )
        }
    }

    @Test
    fun `sends Import with Ente source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Ente) } returns 2

        handler.handle(commandFor(EntryImportType.Ente))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.Ente,
                    entriesCount = 2
                )
            )
        }
    }

    @Test
    fun `sends Import with Google source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Google) } returns 4

        handler.handle(commandFor(EntryImportType.Google))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.Google,
                    entriesCount = 4
                )
            )
        }
    }

    @Test
    fun `sends Import with LastPass source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.LastPass) } returns 7

        handler.handle(commandFor(EntryImportType.LastPass))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.LastPass,
                    entriesCount = 7
                )
            )
        }
    }

    @Test
    fun `sends Import with ProtonAuthenticator source on success`() = runTest {
        coEvery {
            importer.import(contentBytes = any(), importType = EntryImportType.ProtonAuthenticator)
        } returns 10

        handler.handle(commandFor(EntryImportType.ProtonAuthenticator))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.ProtonAuthenticator,
                    entriesCount = 10
                )
            )
        }
    }

    @Test
    fun `sends Import with ProtonPass source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.ProtonPass) } returns 6

        handler.handle(commandFor(EntryImportType.ProtonPass))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.ProtonPass,
                    entriesCount = 6
                )
            )
        }
    }

    @Test
    fun `sends Import with TwoFas source on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.TwoFas) } returns 1

        handler.handle(commandFor(EntryImportType.TwoFas))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.Import(
                    source = AuthenticatorTelemetryEvent.Import.Source.TwoFas,
                    entriesCount = 1
                )
            )
        }
    }

    @Test
    fun `does not send event for Authy even on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Authy) } returns 0

        handler.handle(commandFor(EntryImportType.Authy))

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }

    @Test
    fun `does not send event for Microsoft even on success`() = runTest {
        coEvery { importer.import(contentBytes = any(), importType = EntryImportType.Microsoft) } returns 0

        handler.handle(commandFor(EntryImportType.Microsoft))

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }

    @Test
    fun `does not send event when importer throws exception`() = runTest {
        coEvery {
            importer.import(contentBytes = any(), importType = any())
        } throws IOException("I/O error during import")

        handler.handle(commandFor(EntryImportType.Aegis))

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }
}
