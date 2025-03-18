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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.manual.usecases.GetEntryUseCase
import javax.inject.Inject

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class HomeManualViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEntryUseCase: GetEntryUseCase,
    private val createEntryUseCase: CreateEntryUseCase
) : ViewModel() {

    private val entryFlow: Flow<Entry?> = savedStateHandle
        .getStateFlow<String?>("entryId", null)
        .mapLatest { entryId ->
            if (entryId == null) null
            else getEntryUseCase(entryId)
        }

    private val titleFlow = MutableStateFlow<String?>(value = null)

    private val secretFlow = MutableStateFlow<String?>(value = null)

    private val issuerFlow = MutableStateFlow<String?>(value = null)

    private val digitsFlow = MutableStateFlow<Int?>(value = null)

    private val timeIntervalFlow = MutableStateFlow<Int?>(value = null)

    private val algorithmFlow = MutableStateFlow<EntryAlgorithm?>(value = null)

    private val typeFlow = MutableStateFlow<EntryType?>(value = null)

    internal val stateFlow: StateFlow<HomeManualState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        HomeManualState.create(
            entryFlow = entryFlow,
            titleFlow = titleFlow,
            secretFlow = secretFlow,
            issuerFlow = issuerFlow,
            digitsFlow = digitsFlow,
            timeIntervalFlow = timeIntervalFlow,
            algorithmFlow = algorithmFlow,
            typeFlow = typeFlow
        )
    }

    internal fun onTitleChange(newTitle: String) {
        titleFlow.value = newTitle.trimStart()
    }

    internal fun onSecretChange(newSecret: String) {
        secretFlow.value = newSecret.trim()
    }

    internal fun onIssuerChange(newIssuer: String) {
        issuerFlow.value = newIssuer.trimStart()
    }

    internal fun onDigitsChange(newDigits: Int) {
        digitsFlow.value = newDigits
    }

    internal fun onTimeIntervalChange(newTimeInterval: Int) {
        timeIntervalFlow.value = newTimeInterval
    }

    internal fun onAlgorithmChange(newAlgorithm: EntryAlgorithm) {
        algorithmFlow.value = newAlgorithm
    }

    internal fun onTypeChange(newType: EntryType) {
        typeFlow.value = newType
    }

    internal fun onSubmitForm() {
        viewModelScope.launch {
            createEntryUseCase(formModel = stateFlow.value.formModel)
        }
    }

}
