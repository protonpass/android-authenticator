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

package proton.android.authenticator.business.entries.application.exportall

import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

internal class ExportEntriesCommandHandler @Inject constructor(
    private val entriesExporter: EntriesExporter
) : CommandHandler<ExportEntriesCommand, Int, ExportEntriesReason> {

    override suspend fun handle(command: ExportEntriesCommand): Answer<Int, ExportEntriesReason> = try {
        val result = entriesExporter.export(destinationUri = command.destinationUri)
        AuthenticatorLogger.i(TAG, "Successfully exported $result entries")
        Answer.Success(result)
    } catch (e: AuthenticatorException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not export entries due to authenticator exception",
            reason = ExportEntriesReason.InvalidEntries
        )
    } catch (e: FileNotFoundException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not export entries due to file not found",
            reason = ExportEntriesReason.InvalidEntries
        )
    } catch (e: IOException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not export entries due to IO exception",
            reason = ExportEntriesReason.InvalidEntries
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: ExportEntriesReason
    ): Answer<Int, ExportEntriesReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "ExportEntriesCommandHandler"
    }

}
