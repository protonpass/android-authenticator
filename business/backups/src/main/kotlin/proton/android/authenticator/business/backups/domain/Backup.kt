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

import android.net.Uri
import java.util.concurrent.TimeUnit

data class BackUpRepeatInterval(
    val value: Long,
    val unit: TimeUnit
)

data class Backup(
    val isEnabled: Boolean,
    val frequencyType: BackupFrequencyType,
    val count: Int,
    val lastBackupMillis: Long?,
    val directoryUri: Uri
) {

    val repeatInterval: BackUpRepeatInterval = when (frequencyType) {
        BackupFrequencyType.Daily -> BackUpRepeatInterval(1, TimeUnit.DAYS)
        BackupFrequencyType.Weekly -> BackUpRepeatInterval(7, TimeUnit.DAYS)
        BackupFrequencyType.Monthly -> BackUpRepeatInterval(30, TimeUnit.DAYS)
        BackupFrequencyType.QA -> BackUpRepeatInterval(5, TimeUnit.MINUTES)
    }

    val isBackupLimitReached: Boolean = count >= MAX_BACKUP_COUNT

    val maxBackupCount: Int = MAX_BACKUP_COUNT

    val fileName: String? = lastBackupMillis?.let { "$FILE_PREFIX$it$FILE_SUFFIX" }

    companion object {

        private const val FILE_PREFIX = "proton_authenticator_automatic_backup_"
        private const val FILE_SUFFIX = ".json"

        internal const val MAX_BACKUP_COUNT = 5

        val BACKUP_FILE_REGEX =
            Regex("^${Regex.escape(FILE_PREFIX)}\\d+${Regex.escape(FILE_SUFFIX)}$")

        val Default = Backup(
            isEnabled = false,
            frequencyType = BackupFrequencyType.Daily,
            count = 0,
            lastBackupMillis = null,
            directoryUri = Uri.EMPTY
        )

    }

}
