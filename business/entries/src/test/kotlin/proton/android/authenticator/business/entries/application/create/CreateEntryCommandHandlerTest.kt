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

package proton.android.authenticator.business.entries.application.create

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
import proton.android.authenticator.AuthenticatorEntryModel
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface

@OptIn(ExperimentalCoroutinesApi::class)
internal class CreateEntryCommandHandlerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authenticatorClient: AuthenticatorMobileClientInterface = mockk(relaxed = true)
    private val creator: EntryCreator = mockk(relaxed = true)
    private val telemetryManager: AuthenticatorTelemetryManager = mockk(relaxed = true)

    private val handler = CreateEntryCommandHandler(
        authenticatorClient = authenticatorClient,
        creator = creator,
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

    @Test
    fun `sends CreateEntry with ReadFromExternal source when command is FromUri`() = runTest {
        val entryModel: AuthenticatorEntryModel = mockk(relaxed = true)
        coEvery { authenticatorClient.entryFromUri(any()) } returns entryModel

        handler.handle(CreateEntryCommand.FromUri(uri = "otpauth://totp/test?secret=ABC"))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.CreateEntry(
                    source = AuthenticatorTelemetryEvent.CreateEntry.Source.ReadFromExternal
                )
            )
        }
    }

    @Test
    fun `sends CreateEntry with Manual source when command is FromTotp`() = runTest {
        val entryModel: AuthenticatorEntryModel = mockk(relaxed = true)
        coEvery { authenticatorClient.newTotpEntryFromParams(any()) } returns entryModel
        val command = CreateEntryCommand.FromTotp(
            name = "Test",
            secret = "ABCDEF",
            issuer = "Issuer",
            period = 30,
            digits = 6,
            algorithm = EntryAlgorithm.SHA1
        )

        handler.handle(command)

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.CreateEntry(
                    source = AuthenticatorTelemetryEvent.CreateEntry.Source.Manual
                )
            )
        }
    }

    @Test
    fun `sends CreateEntry with Manual source when command is FromSteam`() = runTest {
        val entryModel: AuthenticatorEntryModel = mockk(relaxed = true)
        coEvery { authenticatorClient.newSteamEntryFromParams(any()) } returns entryModel

        handler.handle(CreateEntryCommand.FromSteam(name = "Steam", secret = "ABCDEF"))

        coVerify {
            telemetryManager.sendEvent(
                AuthenticatorTelemetryEvent.CreateEntry(
                    source = AuthenticatorTelemetryEvent.CreateEntry.Source.Manual
                )
            )
        }
    }

    @Test
    fun `does not send event when creator throws IllegalStateException`() = runTest {
        val entryModel: AuthenticatorEntryModel = mockk(relaxed = true)
        coEvery { authenticatorClient.entryFromUri(any()) } returns entryModel
        coEvery { creator.create(any()) } throws IllegalStateException("cannot save entry")

        handler.handle(CreateEntryCommand.FromUri(uri = "otpauth://totp/test?secret=ABC"))

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }

    @Test
    fun `does not send event when authenticatorClient throws AuthenticatorException`() = runTest {
        coEvery {
            authenticatorClient.newTotpEntryFromParams(any())
        } throws mockk<AuthenticatorException.InvalidSecret>(relaxed = true)
        val command = CreateEntryCommand.FromTotp(
            name = "Test",
            secret = "INVALID",
            issuer = "Issuer",
            period = 30,
            digits = 6,
            algorithm = EntryAlgorithm.SHA1
        )

        handler.handle(command)

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }
}
