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

package proton.android.authenticator.business.backups.application.generate

import proton.android.authenticator.business.backups.domain.BackupFileCreationError
import proton.android.authenticator.business.backups.domain.BackupMissingFileNameError
import proton.android.authenticator.business.backups.domain.BackupNoEntriesError
import proton.android.authenticator.business.backups.domain.BackupNotEnabledError
import proton.android.authenticator.business.shared.domain.errors.ErrorLoggingUtils
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.io.IOException
import javax.inject.Inject

internal class GenerateBackupCommandHandler @Inject constructor(
    private val generator: BackupGenerator
) : CommandHandler<GenerateBackupCommand, Unit, GenerateBackupReason> {

    override suspend fun handle(command: GenerateBackupCommand): Answer<Unit, GenerateBackupReason> = try {
        generator.generate(backupEntries = command.backupEntries)
            .also {
                AuthenticatorLogger.i(TAG, "Successfully generated backup with ${command.backupEntries.size} entries")
            }
            .let(Answer<Unit, GenerateBackupReason>::Success)
    } catch (e: BackupNoEntriesError) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not generate backup due to no entries",
            reason = GenerateBackupReason.NoEntries,
            tag = TAG
        )
    } catch (e: BackupNotEnabledError) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not generate backup due to backup not enabled",
            reason = GenerateBackupReason.NotEnabled,
            tag = TAG
        )
    } catch (e: BackupMissingFileNameError) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not generate backup due to missing file name",
            reason = GenerateBackupReason.MissingFileName,
            tag = TAG
        )
    } catch (e: BackupFileCreationError) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not generate backup due to file creation failure",
            reason = GenerateBackupReason.FileCreationFailed,
            tag = TAG
        )
    } catch (e: IOException) {
        ErrorLoggingUtils.logAndReturnFailure(
            exception = e,
            message = "Could not generate backup due to IO exception",
            reason = GenerateBackupReason.CannotGenerate,
            tag = TAG
        )
    }

    private companion object {

        private const val TAG = "GenerateBackupCommandHandler"

    }

}
