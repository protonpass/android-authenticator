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

package proton.android.authenticator.features.biometrics.activation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.features.biometrics.shared.presentation.BiometricsErrorType
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class BiometricsActivationViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<BiometricsActivationEvent>(
        value = BiometricsActivationEvent.Idle
    )

    internal val stateFlow: StateFlow<BiometricsActivationState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        BiometricsActivationState.create(eventFlow = eventFlow)
    }

    internal fun onConsumeEvent(event: BiometricsActivationEvent) {
        eventFlow.compareAndSet(expect = event, update = BiometricsActivationEvent.Idle)
    }

    internal fun onActivationError(errorType: BiometricsErrorType) {
        when (errorType) {
            BiometricsErrorType.Canceled,
            BiometricsErrorType.UserCanceled -> BiometricsActivationEvent.OnActivationCancelled

            BiometricsErrorType.HardwareNotPresent,
            BiometricsErrorType.HardwareUnavailable,
            BiometricsErrorType.Lockout,
            BiometricsErrorType.LockoutPermanent,
            BiometricsErrorType.NegativeButton,
            BiometricsErrorType.NoBiometrics,
            BiometricsErrorType.NoDeviceCredential,
            BiometricsErrorType.NoSpace,
            BiometricsErrorType.Timeout,
            BiometricsErrorType.UnableToProcess,
            BiometricsErrorType.Unknown,
            BiometricsErrorType.Vendor -> BiometricsActivationEvent.OnActivationFailed
        }.also { event ->
            eventFlow.update { event }
        }
    }

    internal fun onActivationSuccess() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .first()
                .copy(appLockType = SettingsAppLockType.Biometric)
                .let { newSettings -> updateSettingsUseCase(newSettings) }
                .let { answer ->
                    when (answer) {
                        is Answer.Failure -> BiometricsActivationEvent.OnActivationFailed
                        is Answer.Success -> BiometricsActivationEvent.OnActivationSucceeded
                    }
                }
                .also { event -> eventFlow.update { event } }
        }
    }

}
