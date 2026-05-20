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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.shared.usecases.backups.ObserveBackupUseCase
import proton.android.authenticator.features.shared.usecases.backups.UpdateBackupUseCase
import proton.android.authenticator.features.shared.usecases.featureflag.FeatureFlag
import proton.android.authenticator.features.shared.usecases.featureflag.ObserveFeatureFlagUseCase
import proton.android.authenticator.features.shared.usecases.featureflag.SetFeatureFlagOverrideUseCase
import javax.inject.Inject

@HiltViewModel
internal class QaMenuViewModel @Inject constructor(
    private val observeBackupUseCase: ObserveBackupUseCase,
    private val updateBackupUseCase: UpdateBackupUseCase,
    observeFeatureFlagUseCase: ObserveFeatureFlagUseCase,
    private val setFeatureFlagOverrideUseCase: SetFeatureFlagOverrideUseCase
) : ViewModel() {

    private val featureFlagsFlow = combine(
        FeatureFlag.entries.map { flag ->
            observeFeatureFlagUseCase(flag).map { value -> flag to value }
        }
    ) { pairs -> pairs.toMap() }

    internal val stateFlow: StateFlow<QaMasterState> = combine(
        observeBackupUseCase(),
        featureFlagsFlow
    ) { backup, featureFlags ->
        QaMasterState(
            backUpEnabled = backup.isEnabled,
            backUpFrequency = backup.frequencyType,
            featureFlags = featureFlags
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QaMasterState.Default
    )

    fun forceQaFrequency(force: Boolean) {
        viewModelScope.launch {
            val type = if (force) BackupFrequencyType.QA else BackupFrequencyType.Daily
            observeBackupUseCase()
                .first()
                .copy(frequencyType = type)
                .let { updateBackupUseCase(it) }
        }
    }

    fun setFeatureFlagOverride(flag: FeatureFlag, value: Boolean) {
        setFeatureFlagOverrideUseCase(flag = flag, value = value)
    }
}
