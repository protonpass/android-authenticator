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

package proton.android.authenticator.business.backups.application.update

import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.io.IOException
import javax.inject.Inject

internal class UpdateBackupCommandHandler @Inject constructor(
    private val updater: BackupUpdater
) : CommandHandler<UpdateBackupCommand, Unit, UpdateBackupReason> {

    override suspend fun handle(command: UpdateBackupCommand): Answer<Unit, UpdateBackupReason> = try {
        updater.update(backup = command.backup)
        AuthenticatorLogger.i(TAG, "Successfully updated backup")
        Answer.Success(Unit)
    } catch (e: IOException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not update backup due to save failure",
            reason = UpdateBackupReason.CannotSaveBackup
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: UpdateBackupReason
    ): Answer<Unit, UpdateBackupReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "UpdateBackupCommandHandler"
    }

}
