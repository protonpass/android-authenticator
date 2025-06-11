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

package proton.android.authenticator.features.imports.errors.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import proton.android.authenticator.business.entries.application.importall.ImportEntriesReason
import javax.inject.Inject

@HiltViewModel
internal class ImportsErrorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val errorReason = requireNotNull<Int>(savedStateHandle[ARGS_ERROR_REASON])
        .let(enumValues<ImportEntriesReason>()::get)

    internal val stateFlow: StateFlow<ImportsErrorState> = MutableStateFlow(
        ImportsErrorState.create(errorReason = errorReason)
    ).asStateFlow()

    private companion object {

        private const val ARGS_ERROR_REASON = "errorReason"

    }

}
