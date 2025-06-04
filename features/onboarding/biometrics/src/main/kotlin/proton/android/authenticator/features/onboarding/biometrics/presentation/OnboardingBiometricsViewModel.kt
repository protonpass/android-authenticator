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

package proton.android.authenticator.features.onboarding.biometrics.presentation

import android.content.Context
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
import proton.android.authenticator.business.steps.domain.Step
import proton.android.authenticator.business.steps.domain.StepDestination
import proton.android.authenticator.features.onboarding.biometrics.R
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.shared.usecases.biometrics.ObserveBiometricUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.usecases.steps.UpdateStepUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class OnboardingBiometricsViewModel @Inject constructor(
    observeBiometricUseCase: ObserveBiometricUseCase,
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val updateStepUseCase: UpdateStepUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<OnboardingBiometricsEvent>(
        value = OnboardingBiometricsEvent.Idle
    )

    internal val stateFlow: StateFlow<OnboardingBiometricsState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        OnboardingBiometricsState.create(
            biometricFlow = observeBiometricUseCase(),
            eventFlow = eventFlow
        )
    }

    internal fun onConsumeEvent(event: OnboardingBiometricsEvent) {
        eventFlow.compareAndSet(expect = event, update = OnboardingBiometricsEvent.Idle)
    }

    internal fun onEnableBiometric(context: Context) {
        viewModelScope.launch {
            authenticateBiometricUseCase(
                title = context.getString(R.string.onboarding_biometrics_title),
                subtitle = context.getString(R.string.onboarding_biometrics_subtitle),
                context = context
            ).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        eventFlow.update { OnboardingBiometricsEvent.OnEnableFailed }
                    }

                    is Answer.Success -> {
                        observeSettingsUseCase()
                            .first()
                            .copy(appLockType = SettingsAppLockType.Biometric)
                            .let { newSettings -> updateSettingsUseCase(settings = newSettings) }
                            .let { answer ->
                                when (answer) {
                                    is Answer.Failure -> {
                                        OnboardingBiometricsEvent.OnEnableFailed
                                    }

                                    is Answer.Success -> {
                                        updateStepUseCase(step = Step(destination = StepDestination.Home))
                                            .let { answer ->
                                                when (answer) {
                                                    is Answer.Failure -> {
                                                        OnboardingBiometricsEvent.OnEnableFailed
                                                    }

                                                    is Answer.Success -> {
                                                        OnboardingBiometricsEvent.OnEnableSucceeded
                                                    }
                                                }
                                            }
                                    }
                                }
                            }
                            .also { event -> eventFlow.update { event } }
                    }
                }
            }
        }
    }

    internal fun onSkipBiometric() {
        viewModelScope.launch {
            updateStepUseCase(step = Step(destination = StepDestination.Home))
                .let { answer ->
                    when (answer) {
                        is Answer.Failure -> {
                            OnboardingBiometricsEvent.OnSkipFailed
                        }

                        is Answer.Success -> {
                            OnboardingBiometricsEvent.OnSkipSucceeded
                        }
                    }
                }
                .also { event -> eventFlow.update { event } }
        }
    }

}
