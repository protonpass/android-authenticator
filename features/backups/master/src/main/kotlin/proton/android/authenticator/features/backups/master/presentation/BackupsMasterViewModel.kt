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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.backups.master.usecases.ObserveBackupUseCase
import proton.android.authenticator.features.backups.master.usecases.UpdateBackupUseCase
import javax.inject.Inject

@HiltViewModel
internal class BackupsMasterViewModel @Inject constructor(
    observeBackupUseCase: ObserveBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase
) : ViewModel() {

    private val backupModel: BackupMasterModel
        get() = stateFlow.value.backupModel

    internal val stateFlow: StateFlow<BackupsMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        BackupsMasterState.create(backupFlow = observeBackupUseCase())
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

    private fun updateBackup(newBackupMasterBackup: BackupMasterModel) {
        viewModelScope.launch {
            updateBackupUseCase(newBackupMasterBackup.asBackup())
        }
    }

}
