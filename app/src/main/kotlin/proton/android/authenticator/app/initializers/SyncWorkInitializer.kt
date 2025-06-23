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
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import proton.android.authenticator.app.di.ApplicationCoroutineScope
import proton.android.authenticator.app.workers.SyncWorker
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import java.util.concurrent.TimeUnit

internal class SyncWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                SyncWorkManagerInitializer::class.java
            )
        ) {
            combine(
                observeEntryModels(getEntryModelsObserver()),
                observeIsSyncEnabled(getSettingsObserver())
            ) { _, isSyncEnabled -> isSyncEnabled }
                .filter { isSyncEnabled -> isSyncEnabled }
                .onEach { executeSyncWork(getWorkManager()) }
                .launchIn(getApplicationCoroutineScope())
        }
    }

    private fun observeEntryModels(observer: ObserveEntryModelsUseCase) = observer()
        .distinctUntilChanged()

    private fun observeIsSyncEnabled(observer: ObserveSettingsUseCase) = observer()
        .map { settings -> settings.isSyncEnabled }
        .distinctUntilChanged()

    private fun executeSyncWork(workManager: WorkManager) {
        OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .addTag(tag = SYNC_WORK_TAG)
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
            .also(workManager::enqueue)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = listOf(
        WorkManagerInitializer::class.java
    )

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface SyncWorkManagerInitializer {

        @ApplicationCoroutineScope
        fun getApplicationCoroutineScope(): CoroutineScope

        fun getEntryModelsObserver(): ObserveEntryModelsUseCase

        fun getSettingsObserver(): ObserveSettingsUseCase

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val SYNC_WORK_TAG = "sync_work"

        private const val SYNC_WORK_BACKOFF_DELAY_SECONDS = 1L

    }

}
