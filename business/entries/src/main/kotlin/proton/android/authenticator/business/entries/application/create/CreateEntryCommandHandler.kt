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

package proton.android.authenticator.business.entries.application.create

import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class CreateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val creator: EntryCreator
) : CommandHandler<CreateEntryCommand, Unit, CreateEntryReason> {

    override suspend fun handle(command: CreateEntryCommand): Answer<Unit, CreateEntryReason> = try {
        command.toModel(authenticatorClient)
            .let { model -> creator.create(model = model) }
        AuthenticatorLogger.i(TAG, "Successfully created entry")
        Answer.Success(Unit)
    } catch (e: AuthenticatorException.InvalidName) {
        logAndReturnFailure(
            exception = e,
            message = "Could not create entry due to invalid name",
            reason = CreateEntryReason.InvalidEntryTitle
        )
    } catch (e: AuthenticatorException.InvalidSecret) {
        logAndReturnFailure(
            exception = e,
            message = "Could not create entry due to invalid secret",
            reason = CreateEntryReason.InvalidEntrySecret
        )
    } catch (e: AuthenticatorException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not create entry due to authenticator exception",
            reason = CreateEntryReason.Unknown
        )
    } catch (e: IllegalStateException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not create entry due to save failure",
            reason = CreateEntryReason.CannotSaveEntry
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: CreateEntryReason
    ): Answer<Unit, CreateEntryReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "CreateEntryCommandHandler"
    }

}
