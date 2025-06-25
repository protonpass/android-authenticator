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

package proton.android.authenticator.features.backups.master.presentation

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.features.backups.master.usecases.UpdateBackupUseCase
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.backups.GenerateBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

@HiltViewModel
internal class BackupsMasterViewModel @Inject constructor(
    observeBackupUseCase: ObserveBackupUseCase,
    observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val generateBackupUseCase: GenerateBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase
) : ViewModel() {

    private val backupModel: BackupMasterModel
        get() = stateFlow.value.backupModel

    private val backupFlow = observeBackupUseCase()

    private val entryModelsFlow = observeEntryModelsUseCase()
        .map(List<EntryModel>::toPersistentList)

    internal val stateFlow: StateFlow<BackupsMasterState> = combine(
        backupFlow,
        entryModelsFlow
    ) { backup, entryModels ->
        val canCreateBackup = entryModels.isNotEmpty()

        val backupModel = BackupMasterModel(
            isEnabled = backup.isEnabled,
            frequencyType = backup.frequencyType,
            maxBackupCount = backup.maxBackupCount,
            lastBackupMillis = backup.lastBackupMillis,
            count = backup.count,
            directoryUri = backup.directoryUri,
            canCreateBackup = canCreateBackup
        )

        BackupsMasterState(
            backupModel = backupModel,
            entryModels = entryModels
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackupsMasterState(
            backupModel = BackupMasterModel(
                isEnabled = Backup.Default.isEnabled,
                frequencyType = Backup.Default.frequencyType,
                maxBackupCount = Backup.Default.maxBackupCount,
                lastBackupMillis = Backup.Default.lastBackupMillis,
                count = Backup.Default.count,
                directoryUri = Uri.EMPTY,
                canCreateBackup = false
            ),
            entryModels = persistentListOf()
        )
    )

    internal fun onDisableBackup() {
        if (!backupModel.isEnabled) return

        backupModel.copy(
            isEnabled = false,
            directoryUri = Uri.EMPTY
        ).also(::updateBackup)
    }

    internal fun onFolderPicked(uri: Uri) {
        if (backupModel.directoryUri == uri) return
        backupModel.copy(
            isEnabled = true,
            directoryUri = uri
        ).also(::updateBackup)
    }

    internal fun onUpdateFrequencyType(newFrequencyType: BackupFrequencyType) {
        if (backupModel.frequencyType == newFrequencyType) return

        backupModel.copy(frequencyType = newFrequencyType)
            .also(::updateBackup)
    }

    internal fun onCreateBackup(entryModels: List<EntryModel>) {
        viewModelScope.launch {
            generateBackupUseCase(entryModels)
                .let { answer ->
                    when (answer) {
                        is Answer.Failure -> R.string.backups_snackbar_message_backup_error
                        is Answer.Success -> R.string.backups_snackbar_message_backup_success
                    }
                }
                .also { messageResId ->
                    dispatchSnackbarMessage(messageResId = messageResId)
                }
        }
    }

    private fun updateBackup(newBackupMasterBackup: BackupMasterModel) {
        viewModelScope.launch {
            updateBackupUseCase(newBackupMasterBackup.asBackup()).let { answer ->
                when (answer) {
                    is Answer.Failure -> dispatchSnackbarMessage(
                        messageResId = R.string.backups_snackbar_message_update_error
                    )

                    is Answer.Success -> Unit
                }
            }
        }
    }

    private suspend fun dispatchSnackbarMessage(@StringRes messageResId: Int) {
        SnackbarEvent(messageResId = messageResId).also { snackbarEvent ->
            dispatchSnackbarEventUseCase(snackbarEvent)
        }
    }

}
