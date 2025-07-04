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

package proton.android.authenticator.business.entries.application.update

import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class UpdateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val updater: EntryUpdater
) : CommandHandler<UpdateEntryCommand, Unit, UpdateEntryReason> {

    override suspend fun handle(command: UpdateEntryCommand): Answer<Unit, UpdateEntryReason> = try {
        command.toModel(authenticatorClient)
            .let { model ->
                updater.update(
                    id = command.id,
                    position = command.position,
                    model = model
                )
            }
        AuthenticatorLogger.i(TAG, "Successfully updated entry with id: ${command.id}")
        Answer.Success(Unit)
    } catch (e: AuthenticatorException.InvalidName) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update entry due to invalid name",
            reason = UpdateEntryReason.InvalidEntryTitle
        )
    } catch (e: AuthenticatorException.InvalidSecret) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update entry due to invalid secret",
            reason = UpdateEntryReason.InvalidEntrySecret
        )
    } catch (e: AuthenticatorException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update entry due to authenticator exception",
            reason = UpdateEntryReason.Unknown
        )
    } catch (e: IllegalStateException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update entry due to entry not found",
            reason = UpdateEntryReason.EntryNotFound
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: UpdateEntryReason
    ): Answer<Unit, UpdateEntryReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "UpdateEntryCommandHandler"
    }

}
