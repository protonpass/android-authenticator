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

package proton.android.authenticator.business.entries.application.importall

import proton.android.authenticator.commonrust.AuthenticatorImportException
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.io.FileNotFoundException
import javax.inject.Inject

internal class ImportEntriesCommandHandler @Inject constructor(
    private val importer: EntriesImporter
) : CommandHandler<ImportEntriesCommand, Int, ImportEntriesReason> {

    override suspend fun handle(command: ImportEntriesCommand): Answer<Int, ImportEntriesReason> = try {
        val result = importer.import(
            contentUris = command.contentUris,
            importType = command.importType,
            password = command.password
        )
        AuthenticatorLogger.i(TAG, "Successfully imported $result entries")
        Answer.Success(result)
    } catch (e: AuthenticatorImportException.BadContent) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to bad content",
            reason = ImportEntriesReason.BadContent
        )
    } catch (e: AuthenticatorImportException.BadPassword) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to bad password",
            reason = ImportEntriesReason.BadPassword
        )
    } catch (e: AuthenticatorImportException.DecryptionFailed) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to decryption failure",
            reason = ImportEntriesReason.DecryptionFailed
        )
    } catch (e: AuthenticatorImportException.MissingPassword) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to missing password",
            reason = ImportEntriesReason.MissingPassword
        )
    } catch (e: AuthenticatorImportException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to authenticator import exception",
            reason = ImportEntriesReason.BadContent
        )
    } catch (e: FileNotFoundException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to file not found",
            reason = ImportEntriesReason.BadContent
        )
    } catch (e: IllegalArgumentException) {
        logAndReturnFailure(
            exception = e,
            message = "Could not import entries due to illegal argument",
            reason = ImportEntriesReason.BadContent
        )
    }

    private fun logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: ImportEntriesReason
    ): Answer<Int, ImportEntriesReason> {
        AuthenticatorLogger.w(TAG, message)
        AuthenticatorLogger.w(TAG, exception)
        return Answer.Failure(reason = reason)
    }

    private companion object {
        private const val TAG = "ImportEntriesCommandHandler"
    }
}
