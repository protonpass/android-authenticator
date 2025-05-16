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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow

@Immutable
internal class BiometricsActivationState private constructor(
    internal val allowedAuthenticators: Int,
    internal val event: BiometricsActivationEvent
) {

    internal companion object {

        @Composable
        internal fun create(
            allowedAuthenticators: Int,
            eventFlow: MutableStateFlow<BiometricsActivationEvent>
        ): BiometricsActivationState {
            val event by eventFlow.collectAsState(initial = BiometricsActivationEvent.Idle)

            return BiometricsActivationState(
                allowedAuthenticators = allowedAuthenticators,
                event = event
            )
        }

    }

}
