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

package proton.android.authenticator.features.unlock.master.presentation

import android.content.Context
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
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.unlock.master.R
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject
import proton.android.authenticator.shared.ui.R as uiR

@HiltViewModel
internal class UnlockMasterViewModel @Inject constructor(
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<UnlockMasterEvent>(value = UnlockMasterEvent.Idle)

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val stateFlow: StateFlow<UnlockMasterState> = eventFlow
        .mapLatest(::UnlockMasterState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UnlockMasterState.Initial
        )

    internal fun onConsumeEvent(event: UnlockMasterEvent) {
        eventFlow.compareAndSet(expect = event, update = UnlockMasterEvent.Idle)
    }

    internal fun onRequestBiometricAuthentication(context: Context) {
        viewModelScope.launch {
            authenticateBiometricUseCase(
                title = context.getString(
                    R.string.unlock_screen_title,
                    context.getString(uiR.string.authenticator_proton_authenticator)
                ),
                subtitle = context.getString(R.string.unlock_screen_description),
                context = context
            ).fold(
                onFailure = { reason ->
                    AuthenticatorLogger.w(TAG, "App unlock failed: $reason")
                },
                onSuccess = {
                    AuthenticatorLogger.i(TAG, "App unlock succeeded")

                    eventFlow.update { UnlockMasterEvent.OnUnlocked }
                }
            )
        }
    }

    private companion object {

        private const val TAG = "UnlockMasterViewModel"

    }

}
