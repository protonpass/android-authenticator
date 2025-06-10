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

package proton.android.authenticator.business.backups.domain

data class Backup(
    val isEnabled: Boolean,
    val frequencyType: BackupFrequencyType,
    val count: Int,
    val lastBackupMillis: Long?
) {

    val repeatIntervalDays: Long = when (frequencyType) {
        BackupFrequencyType.Daily -> 1
        BackupFrequencyType.Weekly -> 7
        BackupFrequencyType.Monthly -> 30
    }

    val isBackupLimitReached: Boolean = count >= MAX_BACKUP_COUNT

    val maxBackupCount: Int = MAX_BACKUP_COUNT

    val directoryName: String = DIRECTORY_NAME

    internal val fileName: String = "proton_authenticator_automatic_backup_$lastBackupMillis.json"

    internal val path: String = "$directoryName/$fileName"

    companion object {

        private const val DIRECTORY_NAME = "backups"

        internal const val MAX_BACKUP_COUNT = 5

        val Default = Backup(
            isEnabled = false,
            frequencyType = BackupFrequencyType.Daily,
            count = 0,
            lastBackupMillis = null
        )

    }

}
