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

package proton.android.authenticator.features.exports.passwords.presentation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.features.exports.passwords.usecases.ExportEntriesUseCase
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject

@HiltViewModel
internal class ExportsPasswordsViewModel @Inject constructor(
    private val exportEntriesUseCase: ExportEntriesUseCase
) : ViewModel() {

    private val passwordState = mutableStateOf<String?>(value = null)

    private val isPasswordVisibleFlow = MutableStateFlow(value = false)

    private val shouldUsePasswordFlow = MutableStateFlow(value = false)

    private val eventFlow = MutableStateFlow<ExportsPasswordsEvent>(
        value = ExportsPasswordsEvent.Idle
    )

    internal val stateFlow: StateFlow<ExportsPasswordsState> = combine(
        snapshotFlow { passwordState.value.orEmpty() },
        isPasswordVisibleFlow,
        eventFlow,
        ::ExportsPasswordsState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ExportsPasswordsState.Initial
    )

    internal fun onConsumeEvent(event: ExportsPasswordsEvent) {
        eventFlow.compareAndSet(expect = event, update = ExportsPasswordsEvent.Idle)
    }

    internal fun onExportEntries(uri: Uri?, password: String) {
        if (uri == null) {
            AuthenticatorLogger.i(TAG, "Entries export aborted by user")

            return
        }

        viewModelScope.launch {
            exportEntriesUseCase(
                uri = uri,
                password = password.takeIf { shouldUsePasswordFlow.value }
            ).fold(
                onFailure = { reason ->
                    AuthenticatorLogger.w(TAG, "Entries export failed due to: $reason")

                    ExportsPasswordsEvent.OnEntriesExportError(errorReason = reason.ordinal)
                },
                onSuccess = { exportedEntriesCount ->
                    AuthenticatorLogger.i(TAG, "Entries successfully exported. Total: $exportedEntriesCount")

                    ExportsPasswordsEvent.OnEntriesExportSuccess(exportedEntriesCount = exportedEntriesCount)
                }
            ).also { event -> eventFlow.update { event } }
        }
    }

    internal fun onExportEntriesWithPassword() {
        startEntriesExport(shouldUsePassword = true)
    }

    internal fun onExportEntriesWithoutPassword() {
        startEntriesExport(shouldUsePassword = false)
    }

    private fun startEntriesExport(shouldUsePassword: Boolean) {
        shouldUsePasswordFlow.update { shouldUsePassword }

        eventFlow.update {
            ExportsPasswordsEvent.OnEntriesExportStarted(fileName = ExportsPasswordsState.FILE_NAME)
        }
    }

    internal fun onPasswordChange(newPassword: String) {
        passwordState.value = newPassword
    }

    internal fun onPasswordVisibilityChange(newIsVisible: Boolean) {
        isPasswordVisibleFlow.update { newIsVisible }
    }

    private companion object {

        private const val TAG = "ExportsPasswordsViewModel"

    }

}
