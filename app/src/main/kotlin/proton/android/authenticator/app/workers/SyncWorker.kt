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
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.entries.usecases.SyncEntriesModelsUseCase
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger

@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val syncEntriesUseCase: SyncEntriesModelsUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = observeEntryModelsUseCase()
        .first()
        .let { entryModels -> syncEntriesUseCase(entryModels) }
        .fold(
            onFailure = { reason ->
                AuthenticatorLogger.w(tag = TAG, message = "Entries sync failed: $reason")

                Result.failure()
            },
            onSuccess = {
                AuthenticatorLogger.i(tag = TAG, message = "Entries sync succeeded")

                Result.success()
            }
        )

    private companion object {

        private const val TAG = "SyncWorker"

    }

}
