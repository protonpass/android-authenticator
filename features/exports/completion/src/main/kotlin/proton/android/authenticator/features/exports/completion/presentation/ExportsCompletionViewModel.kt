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

package proton.android.authenticator.features.exports.completion.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class ExportsCompletionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exportedEntriesCount: Int = requireNotNull(savedStateHandle[ARGS_EXPORTED_COUNT])

    internal val stateFlow: StateFlow<ExportsCompletionState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        ExportsCompletionState.create(exportedEntriesCount = exportedEntriesCount)
    }

    private companion object {

        private const val ARGS_EXPORTED_COUNT = "exportedEntriesCount"

    }

}
