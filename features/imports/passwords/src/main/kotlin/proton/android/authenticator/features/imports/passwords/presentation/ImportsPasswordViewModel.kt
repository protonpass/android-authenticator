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

package proton.android.authenticator.features.imports.passwords.presentation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.entries.application.importall.ImportEntriesReason
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.shared.usecases.ImportEntriesUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class ImportsPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val importEntriesUseCase: ImportEntriesUseCase
) : ViewModel() {

    private val uri = requireNotNull<String>(savedStateHandle[ARGS_URI])
        .let(Uri::parse)

    private val importType = requireNotNull<Int>(savedStateHandle[ARGS_IMPORT_TYPE])
        .let(enumValues<EntryImportType>()::get)

    private val passwordState = mutableStateOf<String?>(value = null)

    private val isPasswordErrorFlow = MutableStateFlow(value = false)

    private val isPasswordVisibleFlow = MutableStateFlow(value = false)

    private val eventFlow = MutableStateFlow<ImportsPasswordEvent>(
        value = ImportsPasswordEvent.Idle
    )

    internal val stateFlow: StateFlow<ImportsPasswordState> = combine(
        snapshotFlow { passwordState.value },
        isPasswordErrorFlow,
        isPasswordVisibleFlow,
        eventFlow
    ) { password, isPasswordError, isPasswordVisible, event ->
        ImportsPasswordState.create(
            password = password.orEmpty(),
            isPasswordError = isPasswordError,
            isPasswordVisible = isPasswordVisible,
            event = event
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = ImportsPasswordState.create(
            password = "",
            isPasswordError = false,
            isPasswordVisible = false,
            event = ImportsPasswordEvent.Idle
        )
    )

    internal fun onConsumeEvent(event: ImportsPasswordEvent) {
        eventFlow.compareAndSet(expect = event, update = ImportsPasswordEvent.Idle)
    }

    internal fun onPasswordChange(newPassword: String) {
        passwordState.value = newPassword

        isPasswordErrorFlow.update { false }
    }

    internal fun onPasswordVisibilityChange(newIsVisible: Boolean) {
        isPasswordVisibleFlow.update { newIsVisible }
    }

    internal fun onSubmitPassword(password: String) {
        viewModelScope.launch {
            importEntriesUseCase(uri, importType, password).also { answer ->
                when (answer) {
                    is Answer.Failure -> handleImportEntriesFailure(answer)
                    is Answer.Success -> handleImportEntriesSuccess(answer)
                }
            }
        }
    }

    private fun handleImportEntriesFailure(answer: Answer.Failure<Int, ImportEntriesReason>) {
        when (answer.reason) {
            ImportEntriesReason.BadPassword -> {
                isPasswordErrorFlow.update { true }
            }

            ImportEntriesReason.BadContent,
            ImportEntriesReason.DecryptionFailed,
            ImportEntriesReason.MissingPassword -> {
                println("JIBIRI: Password import failed")
            }
        }
    }

    private fun handleImportEntriesSuccess(answer: Answer.Success<Int, ImportEntriesReason>) {
        passwordState.value = null

        eventFlow.update {
            ImportsPasswordEvent.OnFileImported(importedEntriesCount = answer.data)
        }
    }

    private companion object {

        private const val ARGS_URI = "uri"

        private const val ARGS_IMPORT_TYPE = "importType"

    }

}
