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

package proton.android.authenticator.features.home.manual.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.presentation.HomeManualState

@Composable
internal fun HomeManualContent(
    state: HomeManualState,
    onTitleChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onDigitsChange: (Int) -> Unit,
    onTimeIntervalChange: (Int) -> Unit,
    onAlgorithmChange: (EntryAlgorithm) -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onShowAdvanceOptions: () -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    when (formModel.type) {
        EntryType.TOTP -> {
            HomeManualTotpForm(
                modifier = modifier,
                formModel = formModel,
                onTitleChange = onTitleChange,
                onSecretChange = onSecretChange,
                onIssuerChange = onIssuerChange,
                onDigitsChange = onDigitsChange,
                onTimeIntervalChange = onTimeIntervalChange,
                onAlgorithmChange = onAlgorithmChange,
                onTypeChange = onTypeChange,
                onShowAdvanceOptions = onShowAdvanceOptions
            )
        }

        EntryType.STEAM -> {
            HomeManualSteamForm(
                modifier = modifier,
                formModel = formModel,
                onTitleChange = onTitleChange,
                onSecretChange = onSecretChange,
                onTypeChange = onTypeChange
            )
        }
    }
}
