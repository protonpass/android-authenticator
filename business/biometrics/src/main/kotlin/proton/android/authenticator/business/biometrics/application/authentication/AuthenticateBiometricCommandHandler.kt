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

import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.biometrics.domain.BiometricStatus
import proton.android.authenticator.business.biometrics.domain.BiometricStatusError
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class AuthenticateBiometricCommandHandler @Inject constructor(
    private val authenticator: BiometricAuthenticator
) : CommandHandler<AuthenticateBiometricCommand, Unit, AuthenticateBiometricReason> {

    override suspend fun handle(command: AuthenticateBiometricCommand): Answer<Unit, AuthenticateBiometricReason> =
        try {
            authenticator.authenticate(
                title = command.title,
                subtitle = command.subtitle,
                context = command.context
            ).first()
            AuthenticatorLogger.i(TAG, "Successfully authenticated biometric")
            Answer.Success(Unit)
        } catch (error: BiometricStatusError) {
            when (error.status) {
                BiometricStatus.Available ->
                    logAndReturnFailure(
                        exception = error,
                        message = "Could not authenticate biometric due to unknown status",
                        reason = AuthenticateBiometricReason.Unknown
                    )
                BiometricStatus.NotEnrolled ->
                    logAndReturnFailure(
                        exception = error,
                        message = "Could not authenticate biometric due to not enrolled",
                        reason = AuthenticateBiometricReason.NotEnrolled
                    )
                BiometricStatus.Unavailable ->
                    logAndReturnFailure(
                        exception = error,
                        message = "Could not authenticate biometric due to unavailable",
                        reason = AuthenticateBiometricReason.Unavailable
                    )
                BiometricStatus.Unsupported ->
                    logAndReturnFailure(
                        exception = error,
                        message = "Could not authenticate biometric due to unsupported",
                        reason = AuthenticateBiometricReason.Unsupported
                    )
            }
        } catch (e: ClassCastException) {
            logAndReturnFailure(
                exception = e,
                message = "Could not authenticate biometric due to wrong context",
                reason = AuthenticateBiometricReason.WrongContext
            )
        }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: AuthenticateBiometricReason
    ): Answer<Unit, AuthenticateBiometricReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "AuthenticateBiometricCommandHandler"
    }

}
