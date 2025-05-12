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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.R
import proton.android.authenticator.features.home.manual.presentation.HomeManualFormModel
import proton.android.authenticator.shared.ui.domain.components.tabs.FormTab
import proton.android.authenticator.shared.ui.domain.components.textfields.FormPlainTextField
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeManualSteamForm(
    formModel: HomeManualFormModel,
    onTitleChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onTypeChange: (EntryType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemePadding.Small)
    ) {
        FormPlainTextField(
            modifier = Modifier.fillMaxWidth(),
            value = formModel.title,
            label = stringResource(id = R.string.home_manual_form_title_label),
            placeholder = stringResource(id = R.string.home_manual_form_title_placeholder),
            onValueChange = onTitleChange
        )

        FormPlainTextField(
            modifier = Modifier.fillMaxWidth(),
            value = formModel.secret,
            label = stringResource(id = R.string.home_manual_form_secret_label),
            placeholder = stringResource(id = R.string.home_manual_form_secret_label),
            onValueChange = onSecretChange,
            isRequired = true
        )

        Spacer(modifier = Modifier.height(height = ThemeSpacing.Small))

        FormTab(
            title = stringResource(id = R.string.home_manual_form_time_type_title),
            selectedTabIndex = formModel.selectedTypeIndex,
            tabs = formModel.typeOptions,
            onTabSelected = { index ->
                onTypeChange(EntryType.from(value = index))
            }
        )
    }
}
