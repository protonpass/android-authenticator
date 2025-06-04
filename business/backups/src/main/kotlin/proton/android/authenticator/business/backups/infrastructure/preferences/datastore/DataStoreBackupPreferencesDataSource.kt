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

package proton.android.authenticator.business.backups.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.proto.preferences.backups.BackupPreferences
import proton.android.authenticator.proto.preferences.backups.BackupPreferencesFrequency
import javax.inject.Inject

internal class DataStoreBackupPreferencesDataSource @Inject constructor(
    private val backupPreferencesDataStore: DataStore<BackupPreferences>
) : PreferencesDataSource<Backup> {

    override fun observe(): Flow<Backup> = backupPreferencesDataStore.data
        .map { backupPreferences ->
            Backup(
                isEnabled = backupPreferences.isBackupEnabled,
                frequencyType = backupPreferences.frequency.toDomain()
            )
        }

    override suspend fun update(preferences: Backup) {
        backupPreferencesDataStore.updateData { backupPreferences ->
            backupPreferences.toBuilder()
                .setIsBackupEnabled(preferences.isEnabled)
                .setFrequency(preferences.frequencyType.toPreferences())
                .build()
        }
    }

    private fun BackupPreferencesFrequency.toDomain() = when (this) {
        BackupPreferencesFrequency.BACKUP_FREQUENCY_WEEKLY -> BackupFrequencyType.Weekly
        BackupPreferencesFrequency.BACKUP_FREQUENCY_MONTHLY -> BackupFrequencyType.Monthly
        BackupPreferencesFrequency.BACKUP_FREQUENCY_DAILY,
        BackupPreferencesFrequency.UNRECOGNIZED -> BackupFrequencyType.Daily
    }

    private fun BackupFrequencyType.toPreferences() = when (this) {
        BackupFrequencyType.Daily -> BackupPreferencesFrequency.BACKUP_FREQUENCY_DAILY
        BackupFrequencyType.Weekly -> BackupPreferencesFrequency.BACKUP_FREQUENCY_WEEKLY
        BackupFrequencyType.Monthly -> BackupPreferencesFrequency.BACKUP_FREQUENCY_MONTHLY
    }

}
