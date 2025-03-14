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

package proton.android.authenticator.features.settings.master.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
internal fun SettingsSelectorRow(
    title: String,
    selectedOption: String,
    options: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ThemePadding.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(weight = 1f, fill = true),
            text = title,
            color = Theme.colorScheme.textNorm,
            style = Theme.typography.body1Regular
        )

        Text(
            text = selectedOption,
            color = Theme.colorScheme.textNorm,
            style = Theme.typography.body1Regular
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_tiny_right),
            contentDescription = null,
            tint = Theme.colorScheme.textWeak
        )
    }
}
