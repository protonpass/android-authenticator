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

package proton.android.authenticator.features.biometrics.deactivation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.biometrics.deactivation.R
import proton.android.authenticator.features.biometrics.deactivation.presentation.BiometricsDeactivationEvent
import proton.android.authenticator.features.biometrics.deactivation.presentation.BiometricsDeactivationViewModel
import proton.android.authenticator.features.biometrics.shared.ui.BiometricsPromptScreen
import proton.android.authenticator.shared.ui.domain.models.UiText

@Composable
fun BiometricsDeactivationScreen(onCancelled: () -> Unit, onActivated: () -> Unit) {
    with(hiltViewModel<BiometricsDeactivationViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state.event) {
            when (state.event) {
                BiometricsDeactivationEvent.Idle -> Unit

                BiometricsDeactivationEvent.OnDeactivationCancelled,
                BiometricsDeactivationEvent.OnDeactivationFailed -> onCancelled()

                BiometricsDeactivationEvent.OnDeactivationSucceeded -> onActivated()
            }

            onConsumeEvent(event = state.event)
        }

        BiometricsPromptScreen(
            titleText = UiText.Resource(id = R.string.biometrics_deactivation_title),
            subtitleText = UiText.Resource(id = R.string.biometrics_deactivation_subtitle),
            onError = ::onDeactivationError,
            onSuccess = ::onDeactivationSuccess
        )
    }
}
