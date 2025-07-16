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
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.proton.core.presentation.app.AppLifecycleProvider
import proton.android.authenticator.app.workers.KeyWorker
import proton.android.authenticator.features.shared.users.usecases.ObserveIsUserAuthenticatedUseCase
import java.util.concurrent.TimeUnit

internal class KeyWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                KeyWorkInitializerDependencies::class.java
            )
        ) {
            getIsUserAuthenticatedObserver()
                .invoke()
                .distinctUntilChanged()
                .filter { isAuthenticated -> isAuthenticated }
                .onEach { executeKeyWork(getWorkManager()) }
                .flowWithLifecycle(getAppLifecycleProvider().lifecycle)
                .launchIn(getAppLifecycleProvider().lifecycle.coroutineScope)
        }
    }

    private fun executeKeyWork(workManager: WorkManager) {
        OneTimeWorkRequest.Builder(KeyWorker::class.java)
            .addTag(tag = KEY_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                KEY_WORK_BACKOFF_DELAY_SECONDS,
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

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface KeyWorkInitializerDependencies {

        fun getAppLifecycleProvider(): AppLifecycleProvider

        fun getIsUserAuthenticatedObserver(): ObserveIsUserAuthenticatedUseCase

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val KEY_WORK_TAG = "key_work"

        private const val KEY_WORK_BACKOFF_DELAY_SECONDS = 5L

    }

}
