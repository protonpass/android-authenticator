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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.domain.entity.UserId
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.features.shared.entries.usecases.UnsyncEntriesUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger

@HiltWorker
internal class SessionExpiredWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accountManager: AccountManager,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val unsyncEntriesUseCase: UnsyncEntriesUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : CoroutineWorker(context, workerParameters) {

    @Suppress("ReturnCount")
    override suspend fun doWork(): Result {
        val expiredUserId = inputData
            .getString(ARGS_USER_ID)
            ?.let(::UserId)
            ?: run {
                AuthenticatorLogger.i(TAG, "Session expiration handling skipped: no user id provided")

                return Result.success()
            }

        if (accountManager.getAccount(userId = expiredUserId).first() == null) {
            AuthenticatorLogger.i(TAG, "Session expiration handling skipped: no current user available")

            return Result.success()
        }

        observeSettingsUseCase()
            .first()
            .takeIf(Settings::isSyncEnabled)
            ?.copy(isSyncEnabled = false)
            ?.let { settings -> updateSettingsUseCase(settings) }
            ?.also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        AuthenticatorLogger.w(TAG, "Sync disable failed: ${answer.reason}")

                        return Result.failure()
                    }

                    is Answer.Success -> Unit
                }
            }

        unsyncEntriesUseCase().also { answer ->
            when (answer) {
                is Answer.Failure -> {
                    AuthenticatorLogger.w(TAG, "Entries unsync failed: ${answer.reason}")

                    return Result.failure()
                }

                is Answer.Success -> Unit
            }
        }

        return accountManager.removeAccount(userId = expiredUserId)
            .let { Result.success() }
            .also { AuthenticatorLogger.i(TAG, "Session expiration successfully completed") }
    }

    internal companion object {

        private const val TAG = "SessionExpiredWorker"

        internal const val ARGS_USER_ID = "user_id"

    }

}
