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
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.proton.core.presentation.app.AppLifecycleProvider
import proton.android.authenticator.app.workers.SyncWorker
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import java.util.concurrent.TimeUnit

internal class SyncWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                SyncWorkInitializerDependencies::class.java
            )
        ) {
            combine(
                observeIsSyncEnabled(getSettingsObserver()),
                observeEntryModels(getEntryModelsObserver()),
                ::Pair
            )
                .filter { (isSyncEnabled, entryModels) -> isSyncEnabled && entryModels.needsSync() }
                .onEach { executeSyncWork(getWorkManager()) }
                .flowWithLifecycle(getAppLifecycleProvider().lifecycle)
                .launchIn(getAppLifecycleProvider().lifecycle.coroutineScope)
        }
    }

    private fun observeIsSyncEnabled(observer: ObserveSettingsUseCase) = observer()
        .map { settings -> settings.isSyncEnabled }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private fun observeEntryModels(observer: ObserveEntryModelsUseCase) = observer()
        .distinctUntilChanged()
        .debounce(timeoutMillis = SYNC_WORK_DEBOUNCE_MILLIS)

    private fun List<EntryModel>.needsSync() = isEmpty() || any { !it.isSynced || it.isDeleted }

    private fun executeSyncWork(workManager: WorkManager) {
        OneTimeWorkRequest.Builder(SyncWorker::class.java)
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
                workManager.enqueueUniqueWork(
                    uniqueWorkName = SYNC_WORK_NAME,
                    existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE,
                    request = request
                )
            }
            .also(workManager::enqueue)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface SyncWorkInitializerDependencies {

        fun getAppLifecycleProvider(): AppLifecycleProvider

        fun getEntryModelsObserver(): ObserveEntryModelsUseCase

        fun getSettingsObserver(): ObserveSettingsUseCase

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val SYNC_WORK_NAME = "sync_work"

        private const val SYNC_WORK_BACKOFF_DELAY_SECONDS = 1L

        private const val SYNC_WORK_DEBOUNCE_MILLIS = 5_000L

    }

}
