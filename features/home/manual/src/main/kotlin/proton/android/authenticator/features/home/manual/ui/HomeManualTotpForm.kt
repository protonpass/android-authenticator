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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.R
import proton.android.authenticator.features.home.manual.presentation.HomeManualFormModel
import proton.android.authenticator.shared.ui.domain.components.menus.FormDropdownMenu
import proton.android.authenticator.shared.ui.domain.components.menus.FormRevealMenu
import proton.android.authenticator.shared.ui.domain.components.tabs.FormTab
import proton.android.authenticator.shared.ui.domain.components.textfields.FormPlainTextField
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
internal fun HomeManualTotpForm(
    formModel: HomeManualFormModel,
    onTitleChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onDigitsChange: (Int) -> Unit,
    onTimeIntervalChange: (Int) -> Unit,
    onAlgorithmChange: (EntryAlgorithm) -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onShowAdvanceOptions: () -> Unit,
    modifier: Modifier = Modifier
) = with(formModel) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemePadding.Small)
    ) {
        FormPlainTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            label = stringResource(id = R.string.home_manual_form_title_label),
            placeholder = stringResource(id = R.string.home_manual_form_title_placeholder),
            isError = isTitleError,
            isRequired = true,
            onValueChange = onTitleChange
        )

        FormPlainTextField(
            modifier = Modifier.fillMaxWidth(),
            value = secret,
            label = stringResource(id = R.string.home_manual_form_secret_label),
            placeholder = stringResource(id = R.string.home_manual_form_secret_label),
            onValueChange = onSecretChange,
            isError = isSecretError,
            isRequired = true
        )

        FormPlainTextField(
            modifier = Modifier.fillMaxWidth(),
            value = issuer,
            label = stringResource(id = R.string.home_manual_form_issuer_label),
            placeholder = stringResource(id = R.string.home_manual_form_issuer_label),
            onValueChange = onIssuerChange
        )

        FormRevealMenu(
            modifier = Modifier.padding(top = ThemePadding.Medium),
            title = stringResource(id = R.string.home_manual_form_advanced_options_title),
            isRevealed = showAdvanceOptions,
            onClick = onShowAdvanceOptions
        ) {
            Column(
                modifier = Modifier.padding(top = ThemePadding.Medium),
                verticalArrangement = Arrangement.spacedBy(space = ThemePadding.MediumLarge)
            ) {
                FormDropdownMenu(
                    title = stringResource(id = R.string.home_manual_form_digits_title),
                    options = digitsOptions,
                    onSelectedOptionChange = onDigitsChange
                )

                FormDropdownMenu(
                    title = stringResource(id = R.string.home_manual_form_time_interval_title),
                    options = timeIntervalOptions,
                    onSelectedOptionChange = onTimeIntervalChange
                )

                FormTab(
                    title = stringResource(id = R.string.home_manual_form_time_algorithm_title),
                    selectedTabIndex = selectedAlgorithmIndex,
                    tabs = algorithmOptions,
                    onTabSelected = { index ->
                        onAlgorithmChange(EntryAlgorithm.from(value = index))
                    }
                )

                FormTab(
                    title = stringResource(id = R.string.home_manual_form_time_type_title),
                    selectedTabIndex = selectedTypeIndex,
                    tabs = typeOptions,
                    onTabSelected = { index ->
                        onTypeChange(EntryType.from(value = index))
                    }
                )
            }
        }
    }
}
