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

package proton.android.authenticator.business.steps.application.update

import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.io.IOException
import javax.inject.Inject

internal class UpdateStepCommandHandler @Inject constructor(
    private val updater: StepUpdater
) : CommandHandler<UpdateStepCommand, Unit, UpdateStepReason> {

    override suspend fun handle(command: UpdateStepCommand): Answer<Unit, UpdateStepReason> = try {
        updater.update(step = command.step)
        AuthenticatorLogger.i(TAG, "Successfully updated step to: ${command.step}")
        Answer.Success(Unit)
    } catch (e: IOException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update step due to save failure",
            reason = UpdateStepReason.CannotSaveStep
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: UpdateStepReason
    ): Answer<Unit, UpdateStepReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "UpdateStepCommandHandler"
    }

}
