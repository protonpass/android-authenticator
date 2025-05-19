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

package proton.android.authenticator.business.biometrics.application.authentication

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.biometrics.domain.BiometricRepository
import proton.android.authenticator.business.biometrics.domain.BiometricStatus
import proton.android.authenticator.business.biometrics.domain.BiometricStatusError
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

internal class BiometricAuthenticator @Inject constructor(
    private val repository: BiometricRepository
) {

    internal suspend fun authenticate(
        title: String,
        subtitle: String,
        context: Context
    ): Flow<Answer<Unit, AuthenticateBiometricReason>> = getAllowedAuthenticators()
        .let { allowedAuthenticators ->
            createBiometricPromptInfo(title, subtitle, allowedAuthenticators)
        }
        .let { biometricPromptInfo ->
            startAuthentication(context, biometricPromptInfo)
        }

    private suspend fun getAllowedAuthenticators(): Int = repository.find()
        .first()
        .let { biometric ->
            when (biometric.status) {
                BiometricStatus.Available -> biometric.allowedAuthenticators
                BiometricStatus.NotEnrolled,
                BiometricStatus.Unavailable,
                BiometricStatus.Unsupported -> throw BiometricStatusError(status = biometric.status)
            }
        }

    private fun createBiometricPromptInfo(
        title: String,
        subtitle: String,
        allowedAuthenticators: Int
    ) = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setAllowedAuthenticators(allowedAuthenticators)
        .build()

    private fun startAuthentication(context: Context, info: BiometricPrompt.PromptInfo) =
        callbackFlow<Answer<Unit, AuthenticateBiometricReason>> {
            val biometricPrompt = BiometricPrompt(
                context as FragmentActivity,
                ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)

                        when (errorCode) {
                            BiometricPrompt.ERROR_CANCELED -> AuthenticateBiometricReason.Canceled
                            BiometricPrompt.ERROR_HW_NOT_PRESENT -> AuthenticateBiometricReason.HardwareNotPresent
                            BiometricPrompt.ERROR_HW_UNAVAILABLE -> AuthenticateBiometricReason.HardwareUnavailable
                            BiometricPrompt.ERROR_LOCKOUT -> AuthenticateBiometricReason.Lockout
                            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> AuthenticateBiometricReason.LockoutPermanent
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> AuthenticateBiometricReason.NegativeButton
                            BiometricPrompt.ERROR_NO_BIOMETRICS -> AuthenticateBiometricReason.NoBiometrics
                            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> AuthenticateBiometricReason.NoDeviceCredential
                            BiometricPrompt.ERROR_NO_SPACE -> AuthenticateBiometricReason.NoSpace
                            BiometricPrompt.ERROR_TIMEOUT -> AuthenticateBiometricReason.Timeout
                            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> AuthenticateBiometricReason.UnableToProcess
                            BiometricPrompt.ERROR_USER_CANCELED -> AuthenticateBiometricReason.UserCanceled
                            BiometricPrompt.ERROR_VENDOR -> AuthenticateBiometricReason.Vendor
                            else -> AuthenticateBiometricReason.Unknown
                        }.also { authenticateBiometricReason ->
                            trySend(Answer.Failure(authenticateBiometricReason))
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        trySend(Answer.Success(Unit))
                    }

                }
            )

            biometricPrompt.authenticate(info)

            awaitClose {
                biometricPrompt.cancelAuthentication()
            }
        }

}
