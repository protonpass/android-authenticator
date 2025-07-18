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

package proton.android.authenticator.business.keys.application.create

import me.proton.core.crypto.common.pgp.exception.CryptoException
import me.proton.core.network.domain.ApiException
import proton.android.authenticator.business.shared.domain.errors.ErrorLoggingUtils
import proton.android.authenticator.business.shared.domain.infrastructure.network.getErrorCode
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject

internal class CreateKeyCommandHandler @Inject constructor(
    private val creator: KeyCreator
) : CommandHandler<CreateKeyCommand, Unit, CreateKeyReason> {

    override suspend fun handle(command: CreateKeyCommand): Answer<Unit, CreateKeyReason> = try {
        creator.create(userId = command.userId)
        AuthenticatorLogger.i(TAG, "Successfully created key for user: ${command.userId}")
        Answer.Success(Unit)
    } catch (exception: ApiException) {
        if (exception.getErrorCode() == ERROR_CODE_KEY_ALREADY_EXISTS) {
            ErrorLoggingUtils.logAndReturnFailure(
                exception = exception,
                message = "Could not create key due to key already exists",
                reason = CreateKeyReason.KeyAlreadyExists,
                tag = TAG
            )
        } else {
            ErrorLoggingUtils.logAndReturnFailure(
                exception = exception,
                message = "Could not create key due to API call failure",
                reason = CreateKeyReason.ApiCallFailed,
                tag = TAG
            )
        }
    } catch (e: CryptoException) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not create key due to crypto failure",
            reason = CreateKeyReason.CryptoFailed,
            tag = TAG
        )
    } catch (e: IllegalStateException) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not create key due to key generation failure",
            reason = CreateKeyReason.KeyGenerationFailed,
            tag = TAG
        )
    }

    private companion object {
        private const val TAG = "CreateKeyCommandHandler"
        private const val ERROR_CODE_KEY_ALREADY_EXISTS = 2001
    }

}
