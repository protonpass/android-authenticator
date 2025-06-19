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

package proton.android.authenticator.features.sync.master.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.users.usecases.ObserveIsUserAuthenticatedUseCase
import javax.inject.Inject

@HiltViewModel
internal class SyncMasterViewModel @Inject constructor(
    observeIsUserAuthenticatedUseCase: ObserveIsUserAuthenticatedUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<SyncMasterEvent>(value = SyncMasterEvent.Idle)

    private val settingsFlow = observeSettingsUseCase()

    init {
        viewModelScope.launch {
            observeIsUserAuthenticatedUseCase()
                .distinctUntilChanged()
                .collectLatest { isAuthenticated ->
                    if (isAuthenticated) {
                        eventFlow.update { SyncMasterEvent.OnUserAuthenticated }
                    }
                }
        }
    }

    internal val stateFlow: StateFlow<SyncMasterState> = combine(
        eventFlow,
        settingsFlow,
        SyncMasterState::Ready
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SyncMasterState.Loading
    )

    internal fun onConsumeEvent(event: SyncMasterEvent) {
        eventFlow.compareAndSet(expect = event, update = SyncMasterEvent.Idle)
    }

    internal fun onEnableSync(settings: Settings) {
        viewModelScope.launch {
            settings
                .copy(isSyncEnabled = true)
                .let { updatedSettings -> updateSettingsUseCase(settings = updatedSettings) }
                .also { answer ->
                    answer.fold(
                        onSuccess = {
                            eventFlow.update { SyncMasterEvent.OnSyncEnabled }
                        },
                        onFailure = { reason -> }
                    )
                }
        }
    }

}
