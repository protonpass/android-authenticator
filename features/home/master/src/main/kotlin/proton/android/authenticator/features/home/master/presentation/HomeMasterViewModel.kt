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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import app.cash.molecule.moleculeFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import proton.android.authenticator.features.home.master.usecases.DeleteEntryUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntriesUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import proton.android.authenticator.features.shared.usecases.ObserveSettingsUseCase
import javax.inject.Inject
import kotlin.math.floor

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class HomeMasterViewModel @Inject constructor(
    observeEntriesUseCase: ObserveEntriesUseCase,
    observeEntryCodesUseCase: ObserveEntryCodesUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    private val entryCodePeriods = mutableSetOf<Int>()

    private val entrySearchQueryFlow = MutableStateFlow(SEARCH_QUERY_DEFAULT_VALUE)

    @OptIn(FlowPreview::class)
    private val entrySearchQueryDebouncedFlow = entrySearchQueryFlow
        .debounce { entrySearchQuery ->
            if (entrySearchQuery.isEmpty()) SEARCH_QUERY_EMPTY_DEBOUNCE_MILLIS
            else SEARCH_QUERY_DEBOUNCE_MILLIS
        }

    private val entriesFlow = observeEntriesUseCase()
        .onEach { entries ->
            entries.forEach { entry ->
                entryCodePeriods.add(entry.period)
            }
        }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    private val entryCodesFlow = entriesFlow
        .mapLatest { entries ->
            entries.map { entry -> entry.uri }
        }
        .flatMapLatest { entryUris ->
            observeEntryCodesUseCase(entryUris)
        }

    private val entryCodeRemainingTimesFlow = moleculeFlow(mode = RecompositionMode.Immediate) {
        var remainingTimesMap by remember { mutableStateOf(emptyMap<Int, Int>()) }

        LaunchedEffect(Unit) {
            while (isActive) {
                entryCodePeriods.forEach { period ->
                    val remainingTime =
                        period - floor(System.currentTimeMillis().toDouble() / 1000) % period

                    remainingTimesMap += mapOf(period to remainingTime.toInt())
                }

                delay(REMAINING_TIME_INTERVAL_MILLIS)
            }
        }

        remainingTimesMap
    }

    internal val stateFlow: StateFlow<HomeMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        HomeMasterState.create(
            entrySearchQueryFlow = entrySearchQueryDebouncedFlow,
            entriesFlow = entriesFlow,
            entryCodesFlow = entryCodesFlow,
            entryCodesRemainingTimesFlow = entryCodeRemainingTimesFlow,
            settingsFlow = observeSettingsUseCase()
        )
    }

    internal fun onDeleteEntry(entryModel: HomeMasterEntryModel) {
        viewModelScope.launch {
            deleteEntryUseCase(id = entryModel.id)
        }
    }

    internal fun onUpdateEntrySearchQuery(searchQuery: String) {
        entrySearchQueryFlow.value = searchQuery
    }

    private companion object {

        private const val REMAINING_TIME_INTERVAL_MILLIS = 1_000L

        private const val SEARCH_QUERY_DEFAULT_VALUE = ""

        private const val SEARCH_QUERY_EMPTY_DEBOUNCE_MILLIS = 0L

        private const val SEARCH_QUERY_DEBOUNCE_MILLIS = 500L

    }

}
