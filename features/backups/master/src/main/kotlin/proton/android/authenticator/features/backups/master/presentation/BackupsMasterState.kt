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

package proton.android.authenticator.features.backups.master.presentation

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.features.shared.entries.presentation.EntryModel

@Immutable
internal data class BackupsMasterState(
    internal val entryModels: ImmutableList<EntryModel>,
    internal val event: BackupMasterEvent,
    private val backup: Backup
) {

    internal val backupModel: BackupMasterModel = BackupMasterModel(
        isEnabled = backup.isEnabled,
        frequencyType = backup.frequencyType,
        maxBackupCount = backup.maxBackupCount,
        directoryUri = backup.directoryUri,
        count = backup.count,
        lastBackupMillis = backup.lastBackupMillis
    )

    internal val canCreateBackup: Boolean = entryModels.isNotEmpty()

    internal companion object {

        internal val Initial = BackupsMasterState(
            entryModels = persistentListOf(),
            event = BackupMasterEvent.Idle,
            backup = Backup.Default
        )

    }

}
