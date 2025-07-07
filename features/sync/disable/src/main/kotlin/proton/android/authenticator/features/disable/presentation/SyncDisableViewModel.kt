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

package proton.android.authenticator.features.disable.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.users.usecases.DeleteUserUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class SyncDisableViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<SyncDisableEvent>(value = SyncDisableEvent.Idle)

    private val isLoadingFlow = MutableStateFlow(value = false)

    internal val stateFlow: StateFlow<SyncDisableState> = combine(
        eventFlow,
        isLoadingFlow,
        ::SyncDisableState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SyncDisableState.Initial
    )

    internal fun onConsumeEvent(event: SyncDisableEvent) {
        eventFlow.compareAndSet(expect = event, update = SyncDisableEvent.Idle)
    }

    internal fun onDisableSync() {
        viewModelScope.launch {
            isLoadingFlow.update { true }

            deleteUserUseCase().also { answer ->
                when (answer) {
                    is Answer.Success -> Unit
                    is Answer.Failure -> {
                        eventFlow.update { SyncDisableEvent.DisableSyncFailed }

                        return@launch
                    }
                }
            }

            observeSettingsUseCase()
                .first()
                .copy(isSyncEnabled = false)
                .let { settings -> updateSettingsUseCase(settings) }
                .let { answer ->
                    when (answer) {
                        is Answer.Failure -> SyncDisableEvent.DisableSyncFailed
                        is Answer.Success -> SyncDisableEvent.DisableSyncSucceeded
                    }
                }
                .also { event -> eventFlow.update { event } }
        }
    }

}
