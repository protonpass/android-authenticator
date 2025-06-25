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
import proton.android.authenticator.business.backups.application.generate.GenerateBackupReason
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.backups.GenerateBackupUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer

@HiltWorker
internal class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val generateBackupUseCase: GenerateBackupUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = observeEntryModelsUseCase()
        .first()
        .let { entries -> generateBackupUseCase(entries) }
        .let { answer ->
            when (answer) {
                is Answer.Failure -> when (answer.reason) {
                    GenerateBackupReason.MissingFileName,
                    GenerateBackupReason.FileCreationFailed,
                    GenerateBackupReason.CannotGenerate -> Result.failure()
                    GenerateBackupReason.NoEntries,
                    GenerateBackupReason.NotEnabled -> Result.success()
                }

                is Answer.Success -> Result.success()
            }
        }

}
