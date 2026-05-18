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

package proton.android.authenticator.features.imports.menus.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.menus.R
import proton.android.authenticator.features.imports.menus.presentation.ImportsMenuEvent
import proton.android.authenticator.features.imports.menus.presentation.ImportsMenuOption
import proton.android.authenticator.features.imports.menus.presentation.ImportsMenuState
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewProvider

@Composable
internal fun ImportsMenuContent(
    state: ImportsMenuState,
    onOptionSelected: (ImportsMenuOption) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemePadding.Small)
    ) {
        item {
            Text(
                modifier = Modifier.padding(
                    horizontal = ThemePadding.Medium,
                    vertical = ThemePadding.Small
                ),
                text = stringResource(id = R.string.imports_menu_title),
                style = Theme.typography.bodyBold
            )
        }

        items(menuOptions) { menuOption ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(menuOption) }
                    .padding(all = ThemePadding.Medium),
                text = menuOption.titleText.asString(),
                style = Theme.typography.body1Regular
            )
        }
    }
}

@Preview
@Composable
fun ImportsMenuContentPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    Theme(isDarkTheme = isDark) {
        Surface {
            ImportsMenuContent(
                state = ImportsMenuState(
                    event = ImportsMenuEvent.Idle,
                    importType = EntryImportType.Google
                ),
                onOptionSelected = {}
            )
        }
    }
}
