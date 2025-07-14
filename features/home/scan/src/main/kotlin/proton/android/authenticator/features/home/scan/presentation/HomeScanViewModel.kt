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

package proton.android.authenticator.features.home.scan.presentation

import android.net.Uri
import androidx.annotation.StringRes
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
import proton.android.authenticator.business.entries.application.create.CreateEntryReason
import proton.android.authenticator.features.home.scan.R
import proton.android.authenticator.features.home.scan.usecases.CreateEntryUseCase
import proton.android.authenticator.features.home.scan.usecases.ScanEntryQrUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

@HiltViewModel
internal class HomeScanViewModel @Inject constructor(
    private val createEntryUseCase: CreateEntryUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase,
    private val scanEntryQrUseCase: ScanEntryQrUseCase
) : ViewModel() {

    private val hasCameraPermissionFlow = MutableStateFlow<Boolean?>(value = null)

    private val eventFlow = MutableStateFlow<HomeScanEvent>(value = HomeScanEvent.Idle)

    internal val stateFlow: StateFlow<HomeScanState> = combine(
        hasCameraPermissionFlow,
        eventFlow,
        ::HomeScanState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScanState.Initial
    )

    internal fun onConsumeEvent(event: HomeScanEvent) {
        eventFlow.compareAndSet(expect = event, update = HomeScanEvent.Idle)
    }

    internal fun onCameraPermissionRequested(isGranted: Boolean) {
        hasCameraPermissionFlow.update { isGranted }
    }

    internal fun onCreateEntry(uri: String) {
        viewModelScope.launch {
            createEntryUseCase(uri = uri).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        when (answer.reason) {
                            CreateEntryReason.CannotSaveEntry -> {
                                R.string.home_scan_snackbar_message_create_entry_code_error
                            }

                            CreateEntryReason.InvalidEntrySecret -> {
                                R.string.home_scan_snackbar_message_invalid_entry_code
                            }

                            CreateEntryReason.InvalidEntryTitle -> {
                                R.string.home_scan_snackbar_message_invalid_entry_title
                            }

                            CreateEntryReason.Unknown -> {
                                R.string.home_scan_snackbar_message_invalid_entry_unknown
                            }
                        }.also { messageResId -> dispatchSnackbarEvent(messageResId) }
                    }

                    is Answer.Success -> {
                        eventFlow.update { HomeScanEvent.OnEntryCreated }
                    }
                }
            }
        }
    }

    internal fun onScanEntryQr(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            scanEntryQrUseCase(uri = uri).also { entryUriCandidate ->
                entryUriCandidate
                    ?.also(::onCreateEntry)
                    ?: run {
                        dispatchSnackbarEvent(R.string.home_scan_snackbar_message_invalid_qr_code)
                    }
            }
        }
    }

    private suspend fun dispatchSnackbarEvent(@StringRes messageResId: Int) {
        SnackbarEvent(messageResId = messageResId)
            .also { snackbarEvent -> dispatchSnackbarEventUseCase(snackbarEvent) }
    }

}
