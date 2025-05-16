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

package proton.android.authenticator.features.biometrics.shared.presentation

import androidx.biometric.BiometricPrompt

private const val UNKNOWN_ERROR_CODE = -1

enum class BiometricsErrorType(private val value: Int) {
    Canceled(value = BiometricPrompt.ERROR_CANCELED),
    HardwareNotPresent(value = BiometricPrompt.ERROR_HW_NOT_PRESENT),
    HardwareUnavailable(value = BiometricPrompt.ERROR_HW_UNAVAILABLE),
    Lockout(value = BiometricPrompt.ERROR_LOCKOUT),
    LockoutPermanent(value = BiometricPrompt.ERROR_LOCKOUT_PERMANENT),
    NegativeButton(value = BiometricPrompt.ERROR_NEGATIVE_BUTTON),
    NoBiometrics(value = BiometricPrompt.ERROR_NO_BIOMETRICS),
    NoDeviceCredential(value = BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL),
    NoSpace(value = BiometricPrompt.ERROR_NO_SPACE),
    Timeout(value = BiometricPrompt.ERROR_TIMEOUT),
    UnableToProcess(value = BiometricPrompt.ERROR_UNABLE_TO_PROCESS),
    Unknown(value = UNKNOWN_ERROR_CODE),
    UserCanceled(value = BiometricPrompt.ERROR_USER_CANCELED),
    Vendor(value = BiometricPrompt.ERROR_VENDOR);

    companion object {

        fun from(value: Int): BiometricsErrorType = when (value) {
            Canceled.value -> Canceled
            HardwareNotPresent.value -> HardwareNotPresent
            HardwareUnavailable.value -> HardwareUnavailable
            Lockout.value -> Lockout
            LockoutPermanent.value -> LockoutPermanent
            NegativeButton.value -> NegativeButton
            NoBiometrics.value -> NoBiometrics
            NoDeviceCredential.value -> NoDeviceCredential
            NoSpace.value -> NoSpace
            Timeout.value -> Timeout
            UnableToProcess.value -> UnableToProcess
            Unknown.value -> Unknown
            UserCanceled.value -> UserCanceled
            Vendor.value -> Vendor
            else -> Unknown
        }
    }

}
