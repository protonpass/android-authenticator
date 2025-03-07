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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import proton.android.authenticator.features.home.master.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.master.usecases.GetEntryCodeUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntriesUseCase
import javax.inject.Inject

@HiltViewModel
internal class HomeMasterViewModel @Inject constructor(
    private val createEntryUseCase: CreateEntryUseCase,
    private val observeEntriesUseCase: ObserveEntriesUseCase,
    private val getEntryCodeUseCase: GetEntryCodeUseCase
) : ViewModel() {

    internal val stateFlow: StateFlow<HomeMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {

        val entriesResponse by observeEntriesUseCase().collectAsState(emptyList())

        val entryCodesResponse by entriesResponse
            .map { entryResponse ->
                getEntryCodeUseCase(entryResponse.uri)
            }
            .let { entryCodesFlows ->
                combine(entryCodesFlows) { it.toList() }
            }
            .collectAsState(emptyList())

        val entries = entriesResponse.zip(entryCodesResponse) { entryResponse, entryCodeResponse ->
            HomeMasterEntryModel(
                id = entryResponse.id,
                name = entryResponse.name,
                currentCode = entryCodeResponse.currentCode,
                nextCode = entryCodeResponse.nextCode,
                remainingSeconds = entryCodeResponse.remainingSeconds,
                totalSeconds = entryResponse.period
            )
        }

        HomeMasterState(entries = entries)
    }

}
