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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.R
import proton.android.authenticator.features.home.manual.presentation.HomeManualState
import proton.android.authenticator.shared.ui.domain.components.bars.CenterAlignedTopBar
import proton.android.authenticator.shared.ui.domain.components.menus.FormDropdownMenu
import proton.android.authenticator.shared.ui.domain.components.tabs.FormTab
import proton.android.authenticator.shared.ui.domain.components.textfields.FormTextField
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun HomeManualContent(
    state: HomeManualState,
    onNavigationClick: () -> Unit,
    onSubmitFormClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onDigitsChange: (Int) -> Unit,
    onTimeIntervalChange: (Int) -> Unit,
    onAlgorithmChange: (EntryAlgorithm) -> Unit,
    onTypeChange: (EntryType) -> Unit
) = with(state) {
    val digitsOptions = remember(formModel.digitsOptions) {
        formModel.digitsOptions.map(Int::toString)
    }

    val timeIntervalOptions = remember(formModel.timeIntervalOptions) {
        formModel.timeIntervalOptions.map { timeInterval -> "${timeInterval}s" }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopBar(
                title = UiText.Resource(resId = R.string.home_manual_screen_title),
                navigationIcon = UiIcon.Resource(resId = uiR.drawable.ic_arrow_left),
                onNavigationClick = onSubmitFormClick
            )
        }
    ) { paddingValues ->
        HomeManualForm(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues = paddingValues)
                .padding(horizontal = ThemePadding.Medium),
            contents = listOf(
                {
                    FormTextField(
                        modifier = Modifier.fillMaxWidth(),
                        initialValue = formModel.initialTitle,
                        label = stringResource(id = R.string.home_manual_form_title_label),
                        placeholder = stringResource(id = R.string.home_manual_form_title_placeholder),
                        onValueChange = onTitleChange
                    )
                },
                {
                    FormTextField(
                        modifier = Modifier.fillMaxWidth(),
                        initialValue = formModel.initialSecret,
                        label = stringResource(id = R.string.home_manual_form_secret_label),
                        placeholder = stringResource(id = R.string.home_manual_form_secret_label),
                        onValueChange = onSecretChange
                    )
                },
                {
                    FormTextField(
                        modifier = Modifier.fillMaxWidth(),
                        initialValue = formModel.initialIssuer,
                        label = stringResource(id = R.string.home_manual_form_issuer_label),
                        placeholder = stringResource(id = R.string.home_manual_form_issuer_label),
                        onValueChange = onIssuerChange
                    )
                },
                {
                    FormDropdownMenu(
                        title = stringResource(id = R.string.home_manual_form_digits_title),
                        selectedOption = formModel.digits.toString(),
                        options = digitsOptions,
                        onOptionSelected = { index ->
                            onDigitsChange(formModel.digitsOptions[index])
                        }
                    )
                },
                {
                    FormDropdownMenu(
                        title = stringResource(id = R.string.home_manual_form_time_interval_title),
                        selectedOption = "${formModel.timeInterval}s",
                        options = timeIntervalOptions,
                        onOptionSelected = { index ->
                            onTimeIntervalChange(formModel.timeIntervalOptions[index])
                        }
                    )
                },
                {
                    FormTab(
                        title = stringResource(id = R.string.home_manual_form_time_algorithm_title),
                        selectedTabIndex = formModel.selectedAlgorithmIndex,
                        tabs = formModel.algorithmOptions,
                        onTabSelected = { index ->
                            onAlgorithmChange(EntryAlgorithm.from(value = index))
                        }
                    )
                },
                {
                    FormTab(
                        title = stringResource(id = R.string.home_manual_form_time_type_title),
                        selectedTabIndex = formModel.selectedTypeIndex,
                        tabs = formModel.typeOptions,
                        onTabSelected = { index ->
                            onTypeChange(EntryType.from(value = index))
                        }
                    )
                }
            )
        )
    }
}
