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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.biometrics.domain.Biometric
import proton.android.authenticator.business.biometrics.domain.BiometricStatus

@Immutable
internal sealed interface OnboardingBiometricsState {

    @Immutable
    data object Loading : OnboardingBiometricsState

    @Immutable
    data class Ready(private val biometric: Biometric) : OnboardingBiometricsState {

        internal val isBiometricAvailable: Boolean = when (biometric.status) {
            BiometricStatus.Available -> true
            BiometricStatus.NotEnrolled,
            BiometricStatus.Unavailable,
            BiometricStatus.Unsupported -> false
        }

        internal val allowedAuthenticators: Int = biometric.allowedAuthenticators

    }

    companion object {

        @Composable
        internal fun create(biometricFlow: Flow<Biometric?>): OnboardingBiometricsState {
            val biometric by biometricFlow.collectAsState(initial = null)

            return biometric?.let(::Ready) ?: Loading
        }

    }

}
