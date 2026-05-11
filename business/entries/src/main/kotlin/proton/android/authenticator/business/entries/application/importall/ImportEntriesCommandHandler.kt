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

import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.business.shared.domain.errors.ErrorLoggingUtils
import proton.android.authenticator.business.shared.domain.errors.FileTooLargeException
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import proton.android.authenticator.commonrust.AuthenticatorImportException
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

@Suppress("LongMethod")
internal class ImportEntriesCommandHandler @Inject constructor(
    private val importer: EntriesImporter,
    private val telemetryManager: AuthenticatorTelemetryManager
) : CommandHandler<ImportEntriesCommand, Int, ImportEntriesReason> {

    override suspend fun handle(command: ImportEntriesCommand): Answer<Int, ImportEntriesReason> = try {
        when (command) {
            is ImportEntriesCommand.FromBytes -> {
                importer.import(
                    contentBytes = command.contentBytes,
                    importType = command.importType
                )
            }

            is ImportEntriesCommand.FromUris -> {
                importer.import(
                    contentUris = command.contentUris,
                    importType = command.importType,
                    password = command.password
                )
            }
        }.also { entriesCount ->
            AuthenticatorLogger.i(TAG, "Successfully imported $entriesCount entries")
            importSourceFrom(command.importType)?.let { source ->
                telemetryManager.sendEvent(
                    AuthenticatorTelemetryEvent.Import(source = source, entriesCount = entriesCount)
                )
            }
        }.let(Answer<Int, ImportEntriesReason>::Success)
    } catch (e: AuthenticatorImportException.BadContent) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to bad content",
            reason = ImportEntriesReason.BadContent,
            tag = TAG
        )
    } catch (e: AuthenticatorImportException.BadPassword) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to bad password",
            reason = ImportEntriesReason.BadPassword,
            tag = TAG
        )
    } catch (e: AuthenticatorImportException.DecryptionFailed) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to decryption failure",
            reason = ImportEntriesReason.DecryptionFailed,
            tag = TAG
        )
    } catch (e: AuthenticatorImportException.MissingPassword) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to missing password",
            reason = ImportEntriesReason.MissingPassword,
            tag = TAG
        )
    } catch (e: AuthenticatorImportException) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to authenticator import exception",
            reason = ImportEntriesReason.BadContent,
            tag = TAG
        )
    } catch (e: FileNotFoundException) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to file not found",
            reason = ImportEntriesReason.BadContent,
            tag = TAG
        )
    } catch (e: IOException) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to I/O error",
            reason = ImportEntriesReason.BadContent,
            tag = TAG
        )
    } catch (e: FileTooLargeException) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to file too large",
            reason = ImportEntriesReason.FileTooLarge,
            tag = TAG
        )
    } catch (e: IllegalArgumentException) {
        ErrorLoggingUtils.logAndReturnFailure(
            throwable = e,
            message = "Could not import entries due to illegal argument",
            reason = ImportEntriesReason.BadContent,
            tag = TAG
        )
    }

    private fun importSourceFrom(importType: EntryImportType): AuthenticatorTelemetryEvent.Import.Source? =
        when (importType) {
            EntryImportType.Aegis -> AuthenticatorTelemetryEvent.Import.Source.Aegis
            EntryImportType.Bitwarden -> AuthenticatorTelemetryEvent.Import.Source.Bitwarden
            EntryImportType.Ente -> AuthenticatorTelemetryEvent.Import.Source.Ente
            EntryImportType.Google -> AuthenticatorTelemetryEvent.Import.Source.Google
            EntryImportType.LastPass -> AuthenticatorTelemetryEvent.Import.Source.LastPass
            EntryImportType.ProtonAuthenticator -> AuthenticatorTelemetryEvent.Import.Source.ProtonAuthenticator
            EntryImportType.ProtonPass -> AuthenticatorTelemetryEvent.Import.Source.ProtonPass
            EntryImportType.TwoFas -> AuthenticatorTelemetryEvent.Import.Source.TwoFas
            EntryImportType.Authy, EntryImportType.Microsoft -> null
        }

    private companion object {

        private const val TAG = "ImportEntriesCommandHandler"

    }

}
