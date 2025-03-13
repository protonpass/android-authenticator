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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import proton.android.authenticator.features.home.master.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.master.usecases.DeleteEntryUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntriesUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntryCodesUseCase
import javax.inject.Inject
import kotlin.math.floor

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class HomeMasterViewModel @Inject constructor(
    private val createEntryUseCase: CreateEntryUseCase,
    observeEntriesUseCase: ObserveEntriesUseCase,
    observeEntryCodesUseCase: ObserveEntryCodesUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    private val entryCodePeriods = mutableSetOf<Int>()

    private val entriesFlow = observeEntriesUseCase()
        .onEach { entriesResponse ->
            entriesResponse.forEach { entryResponse ->
                entryCodePeriods.add(entryResponse.period)
            }
        }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    private val entryCodesFlow = entriesFlow
        .mapLatest { entriesResponse ->
            entriesResponse.map { entryResponse -> entryResponse.uri }
        }
        .flatMapLatest { entryUris ->
            observeEntryCodesUseCase(entryUris)
        }

    private val entryCodeRemainingTimesFlow = moleculeFlow(mode = RecompositionMode.Immediate) {
        var remainingTimesMap by remember { mutableStateOf(emptyMap<Int, Int>()) }

        LaunchedEffect(Unit) {
            while (isActive) {
                delay(1_000)
                entryCodePeriods.forEach { period ->
                    val remainingTime =
                        period - floor(System.currentTimeMillis().toDouble() / 1000) % period

                    remainingTimesMap += mapOf(period to remainingTime.toInt())
                }
            }
        }

        remainingTimesMap
    }

    internal val stateFlow: StateFlow<HomeMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        val state = HomeMasterState.create(
            entriesFlow = entriesFlow,
            entryCodesFlow = entryCodesFlow,
            entryCodesRemainingTimesFlow = entryCodeRemainingTimesFlow
        )

        state
    }

    internal fun onCopyEntryCode(entryCode: String) {
        println("JIBIRI: copy entry code: $entryCode")
    }

    internal fun onDeleteEntry(entryId: String) {
        viewModelScope.launch {
            deleteEntryUseCase(id = entryId.toInt())
        }
    }

}
