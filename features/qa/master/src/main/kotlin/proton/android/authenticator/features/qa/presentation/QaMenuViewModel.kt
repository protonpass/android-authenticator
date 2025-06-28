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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class QaMenuViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : ViewModel() {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val installationTime: StateFlow<Long?> =
        observeSettingsUseCase()
            .map {
                it.installationTime
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    val formattedInstallationTime: StateFlow<String?> =
        installationTime
            .map {
                if (it != null) {
                    dateFormatter.format(Date(it))
                } else {
                    null
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    suspend fun updateInstallationTime(newValue: Long) {
        observeSettingsUseCase()
            .first()
            .copy(installationTime = newValue)
            .let { updateSettingsUseCase(it) }
            .let { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        print("Failure")
                    }
                    is Answer.Success -> {
                        print("Success")
                    }
                }
            }
    }
}