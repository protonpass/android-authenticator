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

package proton.android.authenticator.features.onboarding.biometrics.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.onboarding.biometrics.presentation.OnboardingBiometricsEvent
import proton.android.authenticator.features.onboarding.biometrics.presentation.OnboardingBiometricsState
import proton.android.authenticator.features.onboarding.biometrics.presentation.OnboardingBiometricsViewModel
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen

@Composable
fun OnboardingBiometricsScreen(onBiometricsEnabled: () -> Unit, onSkipped: () -> Unit) {
    with(hiltViewModel<OnboardingBiometricsViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state.event) {
            when (state.event) {
                OnboardingBiometricsEvent.Idle,
                OnboardingBiometricsEvent.OnEnableFailed,
                OnboardingBiometricsEvent.OnSkipFailed -> Unit

                OnboardingBiometricsEvent.OnEnableSucceeded -> onBiometricsEnabled()
                OnboardingBiometricsEvent.OnSkipSucceeded -> onSkipped()
            }

            onConsumeEvent(event = state.event)
        }

        ScaffoldScreen(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient()
        ) { innerPaddingValues ->
            when (val currentState = state) {
                OnboardingBiometricsState.Loading -> Unit
                is OnboardingBiometricsState.Ready -> {
                    OnboardingBiometricsContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPaddingValues),
                        state = currentState,
                        onBiometricsNotAvailable = ::onSkipBiometric,
                        onEnableBiometricsClick = ::onEnableBiometric,
                        onSkipClick = ::onSkipBiometric
                    )
                }
            }
        }
    }
}
