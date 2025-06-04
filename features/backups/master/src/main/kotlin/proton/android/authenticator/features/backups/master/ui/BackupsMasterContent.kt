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

package proton.android.authenticator.features.backups.master.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.features.backups.master.presentation.BackupsMasterState
import proton.android.authenticator.shared.ui.domain.components.buttons.SecondaryActionButton
import proton.android.authenticator.shared.ui.domain.components.containers.RowsContainer
import proton.android.authenticator.shared.ui.domain.components.rows.NavigationRow
import proton.android.authenticator.shared.ui.domain.components.rows.SelectorRow
import proton.android.authenticator.shared.ui.domain.components.rows.ToggleRow
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun BackupsMasterContent(
    state: BackupsMasterState,
    onIsEnableChange: (Boolean) -> Unit,
    onFrequencyChange: (BackupFrequencyType) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Large)
    ) {
        RowsContainer(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundSection(),
            contents = buildList {
                add(
                    {
                        ToggleRow(
                            titleText = UiText.Resource(id = R.string.backups_automatic_backups_title),
                            isChecked = backupModel.isEnabled,
                            onCheckedChange = onIsEnableChange
                        )
                    }
                )
                if (backupModel.isEnabled) {
                    add(
                        {
                            NavigationRow(
                                titleText = UiText.Resource(id = R.string.backups_folder_location_title),
                                description = UiText.Dynamic(value = "Proton Authenticator Backups/"),
                                showNavigationIcon = true,
                                onClick = {}
                            )
                        }
                    )
                    add(
                        {
                            SelectorRow<BackupFrequencyType>(
                                titleText = UiText.Resource(id = R.string.backups_frequency_title),
                                options = backupModel.frequencyOptions,
                                onSelectedOptionChange = onFrequencyChange
                            )
                        }
                    )
                }
            }
        )

        if (backupModel.isEnabled) {
            SecondaryActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.backups_backup_now_button),
                onClick = {}
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
            ) {
                Text(
                    text = "Only the last 5 backups are kept.",
                    style = Theme.typography.captionRegular,
                    color = Theme.colorScheme.textWeak
                )

                Text(
                    text = "Last backup: Today, 11:42",
                    style = Theme.typography.captionRegular,
                    color = Theme.colorScheme.textWeak
                )
            }
        }
    }
}
