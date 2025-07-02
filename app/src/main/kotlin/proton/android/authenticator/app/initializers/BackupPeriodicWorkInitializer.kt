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

package proton.android.authenticator.app.initializers

import android.content.Context
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import proton.android.authenticator.app.di.ApplicationCoroutineScope
import proton.android.authenticator.app.workers.BackupWorker
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import java.util.concurrent.TimeUnit

internal class BackupPeriodicWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                BackupPeriodicWorkManagerInitializer::class.java
            )
        ) {
            getBackupObserver().invoke()
                .onEach { backup ->
                    if (backup.isEnabled) {
                        schedulePeriodicBackupWork(backup, getWorkManager())
                    } else {
                        cancelPeriodicBackupWork(getWorkManager())
                    }
                }
                .launchIn(getApplicationCoroutineScope())
        }
    }

    private fun schedulePeriodicBackupWork(backup: Backup, workManager: WorkManager) {
        PeriodicWorkRequestBuilder<BackupWorker>(backup.repeatIntervalDays, TimeUnit.DAYS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKUP_WORK_BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()
            .also { request ->
                workManager.enqueueUniquePeriodicWork(
                    uniqueWorkName = BACKUP_WORK_NAME,
                    existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
                    request = request
                )
            }
    }

    private fun cancelPeriodicBackupWork(workManager: WorkManager) {
        workManager.cancelUniqueWork(uniqueWorkName = BACKUP_WORK_NAME)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface BackupPeriodicWorkManagerInitializer {

        @ApplicationCoroutineScope
        fun getApplicationCoroutineScope(): CoroutineScope

        fun getBackupObserver(): ObserveBackupUseCase

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val BACKUP_WORK_NAME = "periodic_backup_work"

        private const val BACKUP_WORK_BACKOFF_DELAY_SECONDS = 60L

    }

}
