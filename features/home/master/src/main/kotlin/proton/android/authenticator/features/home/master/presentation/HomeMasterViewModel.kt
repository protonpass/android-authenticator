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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.application.shared.responses.EntryQueryResponse
import proton.android.authenticator.business.entrycodes.application.shared.responses.EntryCodeQueryResponse
import proton.android.authenticator.features.home.master.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.master.usecases.DeleteEntryUseCase
import proton.android.authenticator.features.home.master.usecases.GetEntryCodeUseCase
import proton.android.authenticator.features.home.master.usecases.ObserveEntriesUseCase
import javax.inject.Inject

@HiltViewModel
internal class HomeMasterViewModel @Inject constructor(
    private val createEntryUseCase: CreateEntryUseCase,
    private val observeEntriesUseCase: ObserveEntriesUseCase,
    private val getEntryCodeUseCase: GetEntryCodeUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    internal val stateFlow: StateFlow<HomeMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        val entries = getEntries()
        val entryCodes = getEntryCodes(entries)
        val state = HomeMasterState.create(
            entries = entries,
            entryCodes = entryCodes
        )

        state
    }

    @Composable
    private fun getEntries(): List<EntryQueryResponse> {
        val entriesResponse by observeEntriesUseCase().collectAsState(emptyList())

        return entriesResponse
    }

    @Composable
    private fun getEntryCodes(entries: List<EntryQueryResponse>): List<EntryCodeQueryResponse> {
        val entryCodesResponse by entries
            .map { entryResponse ->
                getEntryCodeUseCase(entryResponse.uri)
            }
            .let { entryCodesFlows ->
                combine(entryCodesFlows) { it.toList() }
            }
            .collectAsState(emptyList())

        return entryCodesResponse
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
