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

package proton.android.authenticator.features.imports.onboarding.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.application.importall.ImportEntriesReason
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.shared.usecases.ImportEntriesUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class ImportOnboardingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val importEntriesUseCase: ImportEntriesUseCase
) : ViewModel() {

    private val importType = requireNotNull<Int>(savedStateHandle[ARGS_IMPORT_TYPE])
        .let(enumValues<EntryImportType>()::get)

    private val eventFlow =
        MutableStateFlow<ImportOnboardingEvent>(value = ImportOnboardingEvent.Idle)

    internal val stateFlow: StateFlow<ImportOnboardingState> = eventFlow
        .mapLatest { event ->
            ImportOnboardingState(
                event = event,
                importType = importType
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ImportOnboardingState(
                event = ImportOnboardingEvent.Idle,
                importType = importType
            )
        )

    internal fun onEventConsumed(event: ImportOnboardingEvent) {
        eventFlow.compareAndSet(expect = event, update = ImportOnboardingEvent.Idle)
    }

    internal fun onFilesPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            importEntriesUseCase(uris, importType).also { answer ->
                when (answer) {
                    is Answer.Failure -> handleImportEntriesFailure(answer, uris, importType)
                    is Answer.Success -> handleImportEntriesSuccess(answer)
                }
            }
        }
    }

    private fun handleImportEntriesFailure(
        answer: Answer.Failure<Int, ImportEntriesReason>,
        uris: List<Uri>,
        importType: EntryImportType
    ) {
        when (answer.reason) {
            ImportEntriesReason.BadContent,
            ImportEntriesReason.BadPassword,
            ImportEntriesReason.FileTooLarge,
            ImportEntriesReason.DecryptionFailed -> {
                ImportOnboardingEvent.OnFileImportFailed(reason = answer.reason.ordinal)
            }

            ImportEntriesReason.MissingPassword -> {
                uris.firstOrNull()
                    ?.let { uri ->
                        ImportOnboardingEvent.OnFilePasswordRequired(
                            uri = uri.toString(),
                            importType = importType.ordinal
                        )
                    }
                    ?: ImportOnboardingEvent.OnFileImportFailed(reason = answer.reason.ordinal)
            }
        }.also { event -> eventFlow.update { event } }
    }

    private fun handleImportEntriesSuccess(answer: Answer.Success<Int, ImportEntriesReason>) {
        eventFlow.update {
            ImportOnboardingEvent.OnFileImported(importedEntriesCount = answer.data)
        }
    }

    private companion object {

        private const val ARGS_IMPORT_TYPE = "importType"

    }

}
