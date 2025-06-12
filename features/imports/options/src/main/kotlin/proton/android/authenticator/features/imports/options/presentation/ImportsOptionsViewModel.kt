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

package proton.android.authenticator.features.imports.options.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.application.importall.ImportEntriesReason
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.shared.usecases.ImportEntriesUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class ImportsOptionsViewModel @Inject constructor(
    private val importEntriesUseCase: ImportEntriesUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<ImportsOptionsEvent>(value = ImportsOptionsEvent.Idle)

    private val selectedOptionFlow = MutableStateFlow<ImportsOptionsModel?>(value = null)

    internal val stateFlow: StateFlow<ImportsOptionsState> = combine(
        selectedOptionFlow,
        eventFlow,
        ::ImportsOptionsState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImportsOptionsState(
            selectedOptionModel = null,
            event = ImportsOptionsEvent.Idle
        )
    )

    internal fun onEventConsumed(event: ImportsOptionsEvent) {
        eventFlow.compareAndSet(expect = event, update = ImportsOptionsEvent.Idle)
    }

    internal fun onOptionSelected(selectedOptionModel: ImportsOptionsModel) {
        selectedOptionFlow.update { selectedOptionModel }

        eventFlow.update { ImportsOptionsEvent.OnChooseFile(selectedOptionModel.mimeTypes) }
    }

    internal fun onFilePicked(uri: Uri?, importType: EntryImportType?) {
        if (uri == null) return

        if (importType == null) return

        viewModelScope.launch {
            importEntriesUseCase(uri, importType).also { answer ->
                when (answer) {
                    is Answer.Failure -> handleImportEntriesFailure(answer, uri, importType)
                    is Answer.Success -> handleImportEntriesSuccess(answer)
                }
            }
        }
    }

    private fun handleImportEntriesFailure(
        answer: Answer.Failure<Int, ImportEntriesReason>,
        uri: Uri,
        importType: EntryImportType
    ) {
        when (answer.reason) {
            ImportEntriesReason.BadContent,
            ImportEntriesReason.BadPassword,
            ImportEntriesReason.DecryptionFailed -> {
                ImportsOptionsEvent.OnFileImportFailed(answer.reason.ordinal)
            }

            ImportEntriesReason.MissingPassword -> {
                ImportsOptionsEvent.OnFilePasswordRequired(uri.toString(), importType.ordinal)
            }
        }.also { event -> eventFlow.update { event } }
    }

    private fun handleImportEntriesSuccess(answer: Answer.Success<Int, ImportEntriesReason>) {
        eventFlow.update {
            ImportsOptionsEvent.OnFileImported(importedEntriesCount = answer.data)
        }
    }

}
