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

package proton.android.authenticator.app.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import proton.android.authenticator.R
import proton.android.authenticator.business.backups.application.generate.GenerateBackupReason
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.backups.GenerateBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.UpdateBackupUseCase
import proton.android.authenticator.features.shared.usecases.notifications.DispatchNotificationUseCase
import proton.android.authenticator.shared.common.domain.models.NotificationEvent
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import proton.android.authenticator.shared.ui.R as uiR

@HiltWorker
internal class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dispatchNotificationUseCase: DispatchNotificationUseCase,
    private val observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val generateBackupUseCase: GenerateBackupUseCase,
    private val observeBackupUseCase: ObserveBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = observeEntryModelsUseCase()
        .first()
        .let { entries -> generateBackupUseCase(entries) }
        .fold(
            onFailure = { reason ->
                when (reason) {
                    GenerateBackupReason.MissingFileName,
                    GenerateBackupReason.FileCreationFailed,
                    GenerateBackupReason.CannotGenerate -> {
                        AuthenticatorLogger.w(TAG, "Automatic backup failed: $reason")
                            .also { disableAutomaticBackup() }
                            .also { notifyAutomaticBackupCancellation() }
                            .let { Result.failure() }
                    }

                    GenerateBackupReason.NoEntries,
                    GenerateBackupReason.NotEnabled -> {
                        AuthenticatorLogger.i(TAG, "Automatic backup skipped: $reason")
                            .let { Result.success() }
                    }
                }
            },
            onSuccess = {
                AuthenticatorLogger.i(TAG, "Automatic backup successfully generated")
                    .let { Result.success() }
            }
        )

    private suspend fun disableAutomaticBackup() {
        observeBackupUseCase()
            .first()
            .copy(isEnabled = false, directoryUri = Uri.EMPTY)
            .also { disabledBackup -> updateBackupUseCase(newBackup = disabledBackup) }
    }

    private fun notifyAutomaticBackupCancellation() {
        NotificationEvent.Informative(
            iconResId = uiR.drawable.ic_notification,
            title = applicationContext.getString(R.string.notification_automatic_backups_title),
            text = applicationContext.getString(R.string.notification_automatic_backups_message),
            topic = NotificationEvent.Topic.Backups
        ).also(dispatchNotificationUseCase::invoke)
    }

    private companion object {

        private const val TAG = "BackupWorker"

    }

}
