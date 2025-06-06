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

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.business.backups.domain.BackupEntry
import proton.android.authenticator.business.backups.domain.BackupNotEnabledError
import proton.android.authenticator.business.backups.domain.BackupRepository
import proton.android.authenticator.business.shared.di.FileWriterInternal
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryCreator
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntryType
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import javax.inject.Inject

internal class BackupGenerator @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val directoryCreator: DirectoryCreator,
    @FileWriterInternal private val fileWriter: FileWriter,
    private val repository: BackupRepository,
    private val timeProvider: TimeProvider
) {

    internal suspend fun generate(backupEntries: List<BackupEntry>) {
        if (backupEntries.isEmpty()) return

        repository.find()
            .first()
            .also(::ensureBackupIsEnabled)
            .also(::cleanLastBackupIfLimitReached)
            .let(::updateBackup)
            .also { backup -> generateBackup(backup, backupEntries) }
            .also { backup -> repository.save(backup) }
    }

    private fun ensureBackupIsEnabled(backup: Backup) {
        if (backup.isEnabled) return

        throw BackupNotEnabledError()
    }

    private fun cleanLastBackupIfLimitReached(backup: Backup) {
        if (!backup.isBackupLimitReached) return
    }

    private suspend fun generateBackup(backup: Backup, backupEntries: List<BackupEntry>) {
        backupEntries.map(BackupEntry::toModel)
            .let { entryModels ->
                withContext(appDispatchers.default) {
                    authenticatorClient.exportEntries(entryModels)
                }
            }
            .also { backupContent ->
                directoryCreator.create(directoryName = backup.directoryName)
            }
            .also { backupContent ->
                fileWriter.write(
                    path = backup.path,
                    content = backupContent
                )
            }
    }

    private fun updateBackup(backup: Backup) = backup.copy(
        count = backup.count.plus(1),
        lastBackupMillis = timeProvider.currentMillis()
    )

}

private fun BackupEntry.toModel() = AuthenticatorEntryModel(
    id = id,
    name = name,
    issuer = issuer,
    secret = secret,
    uri = uri,
    period = period.toUShort(),
    note = note,
    entryType = enumValues<AuthenticatorEntryType>()[entryTypeOrdinal]
)
