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
        } catch (error: BiometricStatusError) {
            when (error.status) {
                BiometricStatus.Available -> AuthenticateBiometricReason.Unknown
                BiometricStatus.NotEnrolled -> AuthenticateBiometricReason.NotEnrolled
                BiometricStatus.Unavailable -> AuthenticateBiometricReason.Unavailable
                BiometricStatus.Unsupported -> AuthenticateBiometricReason.Unsupported
            }.let(Answer<Unit, AuthenticateBiometricReason>::Failure)
        } catch (_: ClassCastException) {
            Answer.Failure(AuthenticateBiometricReason.WrongContext)
        }

}
