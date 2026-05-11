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

package proton.android.authenticator.features.home.master.presentation

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.applock.ObserveAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.shared.common.domain.providers.TimeProvider

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeMasterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val entryModelsFlow = MutableSharedFlow<List<EntryModel>>(replay = 1)
    private val entryCodesFlows = mutableMapOf<List<String>, MutableSharedFlow<List<EntryCode>>>()
    private val settingsFlow = MutableStateFlow(Settings.Default)

    private val observeEntryModelsUseCase: ObserveEntryModelsUseCase = mockk()
    private val observeEntryCodesUseCase: ObserveEntryCodesUseCase = mockk()
    private val observeSettingsUseCase: ObserveSettingsUseCase = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val observeBackupUseCase: ObserveBackupUseCase = mockk()
    private val observeAppLockStateUseCase: ObserveAppLockStateUseCase = mockk()
    private val telemetryManager: AuthenticatorTelemetryManager = mockk(relaxed = true)

    private lateinit var viewModel: HomeMasterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { observeEntryModelsUseCase.invoke(any()) } returns entryModelsFlow
        every { observeEntryCodesUseCase.invoke(any<List<EntryModel>>()) } answers {
            val entries = firstArg<List<EntryModel>>()
            val uris = entries.map(EntryModel::uri)
            entryCodesFlows.getOrPut(uris) { MutableSharedFlow(replay = 1) }
        }
        every { observeSettingsUseCase.invoke() } returns settingsFlow
        every { timeProvider.remainingPeriodSeconds(any()) } answers { firstArg<Int>() / 2 }
        every { observeBackupUseCase.invoke() } returns MutableSharedFlow()
        every { observeAppLockStateUseCase.invoke() } returns flowOf(AppLockState.AuthNotRequired)

        viewModel = HomeMasterViewModel(
            observeEntryModelsUseCase = observeEntryModelsUseCase,
            observeEntryCodesUseCase = observeEntryCodesUseCase,
            observeSettingsUseCase = observeSettingsUseCase,
            copyToClipboardUseCase = mockk(relaxed = true),
            dispatchSnackbarEventUseCase = mockk(relaxed = true),
            sortEntriesUseCase = mockk(),
            syncEntriesModelsUseCase = mockk(),
            timeProvider = timeProvider,
            observeBackupUseCase = observeBackupUseCase,
            updateBackupUseCase = mockk(relaxed = true),
            updateSettingsUseCase = mockk(relaxed = true),
            observeAppLockStateUseCase = observeAppLockStateUseCase,
            telemetryManager = telemetryManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `new entry appears in state when entries and codes arrive together`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }

        val entries = listOf(createEntryModel("1"), createEntryModel("2"))
        val codes = listOf(createEntryCode("code1"), createEntryCode("code2"))

        entryModelsFlow.emit(entries)
        emitCodesFor(entries, codes)
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        val state = viewModel.stateFlow.value
        assertTrue("State should be Ready, was $state", state is HomeMasterState.Ready)
        val ready = state as HomeMasterState.Ready
        assertEquals(2, ready.entryModels.size)
        assertEquals(listOf("1", "2"), ready.entryModels.map { it.id })
    }

    @Test
    fun `adding a new entry never produces a partial list`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }

        // Initial state with 2 entries
        val initialEntries = listOf(createEntryModel("1"), createEntryModel("2"))
        entryModelsFlow.emit(initialEntries)
        emitCodesFor(initialEntries, listOf(createEntryCode("code1"), createEntryCode("code2")))
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        val initialState = viewModel.stateFlow.value as HomeMasterState.Ready
        assertEquals(2, initialState.entryModels.size)

        // Add a new entry — entries emit but codes for [1,2,3] have NOT arrived yet
        val updatedEntries = initialEntries + createEntryModel("3")
        entryModelsFlow.emit(updatedEntries)
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        // The state must NOT show a partial list with only 2 of the 3 new entries.
        val intermediateState = viewModel.stateFlow.value
        if (intermediateState is HomeMasterState.Ready) {
            val ids = intermediateState.entryModels.map { it.id }
            assertTrue(
                "State must not show partial new entries (got $ids). " +
                    "Expected either old list [1,2] or new complete list [1,2,3].",
                ids == listOf("1", "2") || ids == listOf("1", "2", "3")
            )
        }

        // Now codes arrive for the new entry list
        emitCodesFor(
            updatedEntries,
            listOf(createEntryCode("code1"), createEntryCode("code2"), createEntryCode("code3"))
        )
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        val finalState = viewModel.stateFlow.value as HomeMasterState.Ready
        assertEquals(3, finalState.entryModels.size)
        assertEquals(listOf("1", "2", "3"), finalState.entryModels.map { it.id })
    }

    @Test
    fun `rapidly adding two entries shows all entries once codes arrive`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }

        // Start with 1 entry
        val entry1 = createEntryModel("1")
        entryModelsFlow.emit(listOf(entry1))
        emitCodesFor(listOf(entry1), listOf(createEntryCode("code1")))
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        assertEquals(1, (viewModel.stateFlow.value as HomeMasterState.Ready).entryModels.size)

        // Rapidly add entry 2 — emit entries but NO codes yet
        val entriesWith2 = listOf(entry1, createEntryModel("2"))
        entryModelsFlow.emit(entriesWith2)
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        // Rapidly add entry 3 BEFORE codes for [1,2] arrived
        val entriesWith3 = entriesWith2 + createEntryModel("3")
        entryModelsFlow.emit(entriesWith3)
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        // Codes arrive for the final list [1, 2, 3]
        emitCodesFor(
            entriesWith3,
            listOf(createEntryCode("code1"), createEntryCode("code2"), createEntryCode("code3"))
        )
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        val finalState = viewModel.stateFlow.value as HomeMasterState.Ready
        assertEquals(
            "All 3 entries must be visible",
            listOf("1", "2", "3"),
            finalState.entryModels.map { it.id }
        )
    }

    @Test
    fun `draggable items always match entry models`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }

        val entries = listOf(createEntryModel("1"), createEntryModel("2"))
        entryModelsFlow.emit(entries)
        emitCodesFor(entries, listOf(createEntryCode("c1"), createEntryCode("c2")))
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        val state = viewModel.stateFlow.value as HomeMasterState.Ready
        assertEquals(
            state.entryModels.map { it.id },
            state.draggableItems.map { it.id }
        )
    }

    @Test
    fun `does not send copy_code event when state is not ready`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }
        // Do NOT emit entries — state stays Loading/not-Ready
        testScheduler.runCurrent()

        viewModel.onCopyEntryCode("any-id")
        testScheduler.runCurrent()

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }

    @Test
    fun `does not send copy_code event when entry not found in ready state`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }
        // Emit entries + codes to put state into Ready
        val entries = listOf(createEntryModel("1"))
        entryModelsFlow.emit(entries)
        emitCodesFor(entries, listOf(createEntryCode("code1")))
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        // Call with an ID that doesn't exist in the Ready state
        viewModel.onCopyEntryCode("unknown-id")
        testScheduler.runCurrent()

        coVerify(exactly = 0) { telemetryManager.sendEvent(any()) }
    }

    @Test
    fun `sends copy_code event when entry code is copied`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.stateFlow.collect {} }

        val entries = listOf(createEntryModel("1"))
        val codes = listOf(createEntryCode("code1"))

        entryModelsFlow.emit(entries)
        emitCodesFor(entries, codes)
        testScheduler.advanceTimeBy(2_000)
        testScheduler.runCurrent()

        assertTrue(
            "State should be Ready before testing copy",
            viewModel.stateFlow.value is HomeMasterState.Ready
        )

        viewModel.onCopyEntryCode("1")
        testScheduler.advanceTimeBy(1_000)
        testScheduler.runCurrent()

        coVerify { telemetryManager.sendEvent(AuthenticatorTelemetryEvent.CopyCode) }
    }

    private fun emitCodesFor(entries: List<EntryModel>, codes: List<EntryCode>) {
        val uris = entries.map(EntryModel::uri)
        val flow = entryCodesFlows.getOrPut(uris) { MutableSharedFlow(replay = 1) }
        flow.tryEmit(codes)
    }

    private companion object {

        fun createEntryModel(id: String) = EntryModel(
            id = id,
            name = "Entry $id",
            issuer = "Issuer $id",
            note = null,
            period = 30,
            secret = "secret$id",
            type = EntryType.TOTP,
            uri = "otpauth://totp/Entry$id?secret=secret$id&issuer=Issuer$id",
            algorithm = EntryAlgorithm.SHA1,
            digits = 6,
            iconUrl = null,
            position = id.toInt(),
            createdAt = id.toLong(),
            modifiedAt = id.toLong(),
            isDeleted = false,
            isSynced = true
        )

        fun createEntryCode(code: String) = EntryCode(
            currentCode = code,
            nextCode = "${code}_next"
        )
    }
}
