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

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.features.backups.master.usecases.GenerateBackupUseCase
import proton.android.authenticator.features.backups.master.usecases.ObserveBackupUseCase
import proton.android.authenticator.features.backups.master.usecases.UpdateBackupUseCase
import proton.android.authenticator.features.shared.usecases.entries.ObserveEntriesUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

@HiltViewModel
internal class BackupsMasterViewModel @Inject constructor(
    observeBackupUseCase: ObserveBackupUseCase,
    observeEntriesUseCase: ObserveEntriesUseCase,
    private val generateBackupUseCase: GenerateBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase
) : ViewModel() {

    private val backupModel: BackupMasterModel
        get() = stateFlow.value.backupModel

    private val backupFlow = observeBackupUseCase()

    private val entriesFlow = observeEntriesUseCase()
        .map(List<Entry>::toPersistentList)

    internal val stateFlow: StateFlow<BackupsMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        BackupsMasterState.create(
            backupFlow = backupFlow,
            entriesFlow = entriesFlow
        )
    }

    internal fun onUpdateIsEnabled(newIsEnabled: Boolean) {
        if (backupModel.isEnabled == newIsEnabled) return

        backupModel.copy(isEnabled = newIsEnabled)
            .also(::updateBackup)
    }

    internal fun onUpdateFrequencyType(newFrequencyType: BackupFrequencyType) {
        if (backupModel.frequencyType == newFrequencyType) return

        backupModel.copy(frequencyType = newFrequencyType)
            .also(::updateBackup)
    }

    internal fun onCreateBackup(entries: List<Entry>) {
        viewModelScope.launch {
            generateBackupUseCase(entries)
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
