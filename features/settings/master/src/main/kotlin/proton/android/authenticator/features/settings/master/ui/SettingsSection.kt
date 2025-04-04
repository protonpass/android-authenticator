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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun SettingsSection(title: String, contents: List<@Composable () -> Unit>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium)
    ) {
        Text(
            modifier = Modifier.padding(start = ThemeSpacing.Medium),
            text = title,
            color = Theme.colorScheme.textWeak,
            style = Theme.typography.body2Medium
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundSection()
        ) {
            contents.forEachIndexed { index, content ->
                content()

                if (index < contents.lastIndex) {
                    HorizontalDivider(
                        color = Theme.colorScheme.menuListBorder
                    )
                }
            }
        }
    }
}
