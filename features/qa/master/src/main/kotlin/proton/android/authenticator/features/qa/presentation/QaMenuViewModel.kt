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

package proton.android.authenticator.features.qa.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.UpdateBackupUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class QaMenuViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val observeBackupUseCase: ObserveBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase
) : ViewModel() {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    internal val stateFlow: StateFlow<QaMasterState> = combine(
        observeSettingsUseCase(),
        observeBackupUseCase()
    ) { settings, backup ->
        val installationTime = settings.installationTime
        val formattedInstallationTime = installationTime?.let {
            dateFormatter.format(Date(it))
        }
        QaMasterState(
            installationTime = installationTime,
            formattedInstallationTime = formattedInstallationTime,
            backUpEnabled = backup.isEnabled,
            backUpFrequency = backup.frequencyType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QaMasterState.Default
    )

    suspend fun updateInstallationTime(newValue: Long) {
        observeSettingsUseCase()
            .first()
            .copy(installationTime = newValue)
            .let { updateSettingsUseCase(it) }
    }

    suspend fun forceQaFrequency(force: Boolean) {
        val type = if (force) BackupFrequencyType.QA else BackupFrequencyType.Daily
        observeBackupUseCase()
            .first()
            .copy(frequencyType = type)
            .let { updateBackupUseCase(it) }
    }
}
