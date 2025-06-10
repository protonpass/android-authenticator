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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.features.shared.entries.presentation.EntryModel

@Immutable
internal class BackupsMasterState private constructor(
    internal val backupModel: BackupMasterModel,
    internal val entryModels: ImmutableList<EntryModel>
) {

    internal companion object {

        @Composable
        internal fun create(
            backupFlow: Flow<Backup>,
            entryModelsFlow: Flow<ImmutableList<EntryModel>>
        ): BackupsMasterState {
            val backup by backupFlow.collectAsState(initial = Backup.Default)
            val entryModels by entryModelsFlow.collectAsState(initial = persistentListOf())

            val canCreateBackup = remember(key1 = entryModels) {
                entryModels.isNotEmpty()
            }

            val backupModel = remember(key1 = backup, key2 = canCreateBackup) {
                BackupMasterModel(
                    isEnabled = backup.isEnabled,
                    frequencyType = backup.frequencyType,
                    maxBackupCount = backup.maxBackupCount,
                    lastBackupMillis = backup.lastBackupMillis,
                    count = backup.count,
                    path = backup.directoryName,
                    canCreateBackup = canCreateBackup
                )
            }

            return BackupsMasterState(
                backupModel = backupModel,
                entryModels = entryModels
            )
        }

    }

}
