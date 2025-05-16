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

package proton.android.authenticator.business.biometrics.infrastructure

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import proton.android.authenticator.business.biometrics.domain.Biometric
import proton.android.authenticator.business.biometrics.domain.BiometricRepository
import proton.android.authenticator.business.biometrics.domain.BiometricStatus

internal class BiometricManagerBiometricRepository @Inject constructor(
    private val biometricManager: BiometricManager
) : BiometricRepository {

    private val allowedAuthenticator: Int = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        BIOMETRIC_WEAK
    } else {
        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    }

    override fun find(): Flow<Biometric> = biometricManager.canAuthenticate(allowedAuthenticator)
        .let { authenticators ->
            when (authenticators) {
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.Unavailable
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NotEnrolled
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.Unsupported
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
                else -> BiometricStatus.Unsupported
            }
        }
        .let { status -> Biometric(status = status, allowedAuthenticators = allowedAuthenticator) }
        .let(::flowOf)

}
