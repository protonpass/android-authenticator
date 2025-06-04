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

import proton.android.authenticator.business.backups.domain.BackupFrequencyType
import proton.android.authenticator.features.backups.master.R
import proton.android.authenticator.shared.ui.domain.models.UiSelectorOption
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface BackupsMasterFrequencyOption : UiSelectorOption<BackupFrequencyType> {

    data class Daily(override val selectedType: BackupFrequencyType) : BackupsMasterFrequencyOption {

        override val isSelected: Boolean = selectedType == BackupFrequencyType.Daily

        override val text: UiText = UiText.Resource(id = R.string.backups_frequency_option_daily)

        override val value: BackupFrequencyType = BackupFrequencyType.Daily

    }

    data class Weekly(override val selectedType: BackupFrequencyType) : BackupsMasterFrequencyOption {

        override val isSelected: Boolean = selectedType == BackupFrequencyType.Weekly

        override val text: UiText = UiText.Resource(id = R.string.backups_frequency_option_weekly)

        override val value: BackupFrequencyType = BackupFrequencyType.Weekly

    }

    data class Monthly(override val selectedType: BackupFrequencyType) : BackupsMasterFrequencyOption {

        override val isSelected: Boolean = selectedType == BackupFrequencyType.Monthly

        override val text: UiText = UiText.Resource(id = R.string.backups_frequency_option_monthly)

        override val value: BackupFrequencyType = BackupFrequencyType.Monthly

    }

}
