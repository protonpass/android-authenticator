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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import proton.android.authenticator.app.di.ApplicationCoroutineScope
import proton.android.authenticator.app.workers.SyncWorker
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import java.util.concurrent.TimeUnit

internal class SyncPeriodicWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                SyncPeriodicWorkManagerInitializer::class.java
            )
        ) {
            getSettingsObserver().invoke()
                .map { settings -> settings.isSyncEnabled }
                .distinctUntilChanged()
                .onEach { isSyncEnabled ->
                    if (isSyncEnabled) {
                        schedulePeriodicSyncWork(getWorkManager())
                    } else {
                        cancelPeriodicSyncWork(getWorkManager())
                    }
                }
                .launchIn(getApplicationCoroutineScope())
        }
    }

    private fun schedulePeriodicSyncWork(workManager: WorkManager) {
        PeriodicWorkRequestBuilder<SyncWorker>(SYNC_WORK_REPEAT_INTERVAL_DAYS, TimeUnit.DAYS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                SYNC_WORK_BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
            .also { request ->
                workManager.enqueueUniquePeriodicWork(
                    uniqueWorkName = SYNC_WORK_NAME,
                    existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                    request = request
                )
            }
    }

    private fun cancelPeriodicSyncWork(workManager: WorkManager) {
        workManager.cancelUniqueWork(uniqueWorkName = SYNC_WORK_NAME)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface SyncPeriodicWorkManagerInitializer {

        @ApplicationCoroutineScope
        fun getApplicationCoroutineScope(): CoroutineScope

        fun getSettingsObserver(): ObserveSettingsUseCase

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val SYNC_WORK_NAME = "periodic_sync_work"

        private const val SYNC_WORK_REPEAT_INTERVAL_DAYS = 1L

        private const val SYNC_WORK_BACKOFF_DELAY_SECONDS = 60L

    }

}
