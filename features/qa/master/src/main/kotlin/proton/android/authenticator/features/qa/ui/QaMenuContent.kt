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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.qa.presentation.QaMasterState
import proton.android.authenticator.features.shared.usecases.featureflag.FeatureFlag
import proton.android.authenticator.shared.ui.domain.theme.Theme

@Composable
internal fun QaMenuContent(
    state: QaMasterState,
    onForceQaFrequency: (Boolean) -> Unit,
    onSetFeatureFlagOverride: (FeatureFlag, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            BackUpRow(
                isEnabled = state.backUpEnabled,
                frequencyType = state.backUpFrequency,
                onForceQaFrequency = onForceQaFrequency
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))


            state.featureFlags.forEach { (flag, isEnabled) ->
                FeatureFlagRow(
                    flag = flag,
                    isEnabled = isEnabled,
                    onToggle = { newValue -> onSetFeatureFlagOverride(flag, newValue) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun FeatureFlagRow(
    flag: FeatureFlag,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(
                text = flag.title,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.body1Medium
            )
            Text(
                text = flag.description,
                color = Theme.colorScheme.textWeak,
                style = Theme.typography.captionRegular
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
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
    }
}
