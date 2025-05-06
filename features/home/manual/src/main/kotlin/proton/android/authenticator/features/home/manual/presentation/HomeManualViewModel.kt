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

package proton.android.authenticator.features.home.manual.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.manual.usecases.GetEntryUseCase
import proton.android.authenticator.features.home.manual.usecases.UpdateEntryUseCase
import javax.inject.Inject

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class HomeManualViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEntryUseCase: GetEntryUseCase,
    private val createEntryUseCase: CreateEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase
) : ViewModel() {

    private val entryId: String? = savedStateHandle["entryId"]

    private val entryFlow = flow {
        entryId
            ?.let { id -> getEntryUseCase(id) }
            .also { entry -> emit(entry) }
    }

    private val eventFlow = MutableStateFlow<HomeManualEvent>(value = HomeManualEvent.Idle)

    private val titleState = mutableStateOf<String?>(value = null)

    private val secretState = mutableStateOf<String?>(value = null)

    private val issuerState = mutableStateOf<String?>(value = null)

    private val digitsFlow = MutableStateFlow<Int?>(value = null)

    private val timeIntervalFlow = MutableStateFlow<Int?>(value = null)

    private val algorithmFlow = MutableStateFlow<EntryAlgorithm?>(value = null)

    private val typeFlow = MutableStateFlow<EntryType?>(value = null)

    private val showAdvanceOptionsFlow = MutableStateFlow<Boolean?>(value = null)

    internal val stateFlow: StateFlow<HomeManualState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        HomeManualState.create(
            entryId = entryId,
            entryFlow = entryFlow,
            title = titleState.value,
            secret = secretState.value,
            issuer = issuerState.value,
            digitsFlow = digitsFlow,
            timeIntervalFlow = timeIntervalFlow,
            algorithmFlow = algorithmFlow,
            typeFlow = typeFlow,
            showAdvanceOptionsFlow = showAdvanceOptionsFlow,
            eventFlow = eventFlow
        )
    }

    internal fun onEventConsumed(event: HomeManualEvent) {
        eventFlow.compareAndSet(expect = event, update = HomeManualEvent.Idle)
    }

    internal fun onTitleChange(newTitle: String) {
        titleState.value = newTitle
    }

    internal fun onSecretChange(newSecret: String) {
        secretState.value = newSecret.trim()
    }

    internal fun onIssuerChange(newIssuer: String) {
        issuerState.value = newIssuer.trimStart()
    }

    internal fun onDigitsChange(newDigits: Int) {
        digitsFlow.update { newDigits }
    }

    internal fun onTimeIntervalChange(newTimeInterval: Int) {
        timeIntervalFlow.update { newTimeInterval }
    }

    internal fun onAlgorithmChange(newAlgorithm: EntryAlgorithm) {
        algorithmFlow.update { newAlgorithm }
    }

    internal fun onTypeChange(newType: EntryType) {
        typeFlow.update { newType }
        showAdvanceOptionsFlow.update { true }
    }

    internal fun onShowAdvanceOptions() {
        showAdvanceOptionsFlow.update { true }
    }

    internal fun onSubmitForm(formModel: HomeManualFormModel) {
        if (entryId == null) {
            createEntry(formModel)
        } else {
            updateEntry(formModel)
        }
    }

    private fun createEntry(formModel: HomeManualFormModel) {
        viewModelScope.launch {
            createEntryUseCase(formModel = formModel)

            eventFlow.update { HomeManualEvent.OnEntryCreated }
        }
    }

    private fun updateEntry(formModel: HomeManualFormModel) {
        if (entryId == null) return

        viewModelScope.launch {
            updateEntryUseCase(entryId = entryId, formModel = formModel)

            eventFlow.update { HomeManualEvent.OnEntryUpdated }
        }
    }

}
