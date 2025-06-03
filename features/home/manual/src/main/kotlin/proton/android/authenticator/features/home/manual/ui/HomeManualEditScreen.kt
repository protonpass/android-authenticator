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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.R
import proton.android.authenticator.features.home.manual.presentation.HomeManualFormModel
import proton.android.authenticator.features.home.manual.presentation.HomeManualState
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun HomeManualEditScreen(
    state: HomeManualState.Editing,
    onNavigationClick: () -> Unit,
    onSubmitForm: (HomeManualFormModel) -> Unit,
    onTitleChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onDigitsChange: (Int) -> Unit,
    onTimeIntervalChange: (Int) -> Unit,
    onAlgorithmChange: (EntryAlgorithm) -> Unit,
    onTypeChange: (EntryType) -> Unit,
    onShowAdvanceOptions: () -> Unit
) = with(state) {
    ScaffoldScreen(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        topBar = {
            SmallTopBar(
                title = UiText.Resource(id = R.string.home_manual_screen_title_edit),
                navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_cross),
                onNavigationClick = onNavigationClick,
                action = UiText.Resource(id = uiR.string.action_save),
                isActionEnabled = state.formModel.isValid,
                onActionClick = { onSubmitForm(formModel) }
            )
        }
    ) { paddingValues ->
        HomeManualContent(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues = paddingValues)
                .padding(horizontal = ThemePadding.Medium),
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
}
