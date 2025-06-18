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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import proton.android.authenticator.features.shared.auth.usecases.LaunchSignInFlowUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import javax.inject.Inject

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class SyncMasterViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val launchSignInFlowUseCase: LaunchSignInFlowUseCase
) : ViewModel() {

    private val settingsFlow = observeSettingsUseCase()

    internal val stateFlow: StateFlow<SyncMasterState> = settingsFlow
        .mapLatest { settings ->
            SyncMasterState.Ready(settingsThemeType = settings.themeType)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SyncMasterState.Loading
        )

    internal fun onSignIn() {
        launchSignInFlowUseCase()
    }

    internal fun onSignUp() {
        println("JIBIRI: onSignUp")
    }

}
