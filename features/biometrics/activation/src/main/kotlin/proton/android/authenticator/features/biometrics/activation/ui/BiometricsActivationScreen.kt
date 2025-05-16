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

package proton.android.authenticator.features.biometrics.activation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.biometrics.activation.R
import proton.android.authenticator.features.biometrics.activation.presentation.BiometricsActivationEvent
import proton.android.authenticator.features.biometrics.activation.presentation.BiometricsActivationViewModel
import proton.android.authenticator.features.biometrics.shared.ui.BiometricsPrompt
import proton.android.authenticator.shared.ui.domain.models.UiText

@Composable
fun BiometricsActivationScreen(onCancelled: () -> Unit, onActivated: () -> Unit) =
    with(hiltViewModel<BiometricsActivationViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state.event) {
            when (state.event) {
                BiometricsActivationEvent.Idle -> Unit

                BiometricsActivationEvent.OnActivationCancelled,
                BiometricsActivationEvent.OnActivationFailed -> onCancelled()

                BiometricsActivationEvent.OnActivationSucceeded -> onActivated()
            }

            onConsumeEvent(event = state.event)
        }

        BiometricsPrompt(
            titleText = UiText.Resource(id = R.string.biometrics_activation_title),
            subtitleText = UiText.Resource(id = R.string.biometrics_activation_subtitle),
            allowedAuthenticators = state.allowedAuthenticators,
            onError = ::onActivationError,
            onSuccess = ::onActivationSuccess
        )
    }
