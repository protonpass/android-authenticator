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

package proton.android.authenticator.features.qa.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.qa.presentation.QaMenuViewModel
import proton.android.authenticator.shared.ui.domain.theme.Theme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QaMenuContent(modifier: Modifier, viewModel: QaMenuViewModel) = with(viewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            InstallationTimeRow(
                formattedInstallationTime = state.formattedInstallationTime,
                onClick = { showDatePicker = true }
            )

            BackUpRow(
                isEnabled = state.backUpEnabled,
                frequencyType = state.backUpFrequency,
                onForceQaFrequency = { force ->
                    scope.launch {
                        viewModel.forceQaFrequency(force)
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.installationTime,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedLocalDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val today = LocalDate.now(ZoneId.systemDefault())
                    return !selectedLocalDate.isAfter(today)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    scope.launch {
                        datePickerState.selectedDateMillis?.let {
                            updateInstallationTime(it)
                        }
                    }
                }) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun InstallationTimeRow(formattedInstallationTime: String?, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Installation time")

        Spacer(modifier = Modifier.weight(1f))

        formattedInstallationTime?.let {
            TextButton(onClick = onClick) {
                Text(text = it)
            }
        }
    }
}

@Composable
private fun BackUpRow(
    isEnabled: Boolean,
    frequencyType: BackupFrequencyType,
    onForceQaFrequency: (Boolean) -> Unit
) {
    if (isEnabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Force 5 minutes frequency")

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = frequencyType == BackupFrequencyType.QA,
                onCheckedChange = onForceQaFrequency
            )
        }
    } else {
        Text(
            text = "Back up is not enabled",
            color = Theme.colorScheme.textWeak
        )
    }
}
