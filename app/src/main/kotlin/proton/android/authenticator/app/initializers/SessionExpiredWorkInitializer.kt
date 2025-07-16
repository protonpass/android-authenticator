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
import androidx.lifecycle.Lifecycle
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import me.proton.core.account.domain.entity.Account
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.accountmanager.presentation.observe
import me.proton.core.accountmanager.presentation.onSessionForceLogout
import me.proton.core.presentation.app.AppLifecycleProvider
import proton.android.authenticator.app.workers.SessionExpiredWorker
import proton.android.authenticator.app.workers.SessionExpiredWorker.Companion.ARGS_USER_ID
import java.util.concurrent.TimeUnit

internal class SessionExpiredWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                SessionExpiredWorkInitializerDependencies::class.java
            )
        ) {
            getAccountManager()
                .observe(
                    lifecycle = getAppLifecycleProvider().lifecycle,
                    minActiveState = Lifecycle.State.CREATED
                )
                .onSessionForceLogout { account ->
                    executeSessionExpiredWork(account, getWorkManager())
                }
        }
    }

    private fun executeSessionExpiredWork(account: Account, workManager: WorkManager) {
        OneTimeWorkRequest.Builder(SessionExpiredWorker::class.java)
            .addTag(tag = SESSION_EXPIRED_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                SESSION_EXPIRED_BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString(ARGS_USER_ID, account.userId.id)
                    .build()
            )
            .build()
            .also(workManager::enqueue)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface SessionExpiredWorkInitializerDependencies {

        fun getAccountManager(): AccountManager

        fun getAppLifecycleProvider(): AppLifecycleProvider

        fun getWorkManager(): WorkManager

    }

    private companion object {

        private const val SESSION_EXPIRED_TAG = "session_expired_work"

        private const val SESSION_EXPIRED_BACKOFF_DELAY_SECONDS = 5L

    }

}
