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

package proton.android.authenticator.features.imports.options.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.imports.options.presentation.ImportsOptionsViewModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen

@Composable
fun ImportsOptionsScreen(onNavigationClick: () -> Unit, onImportTypeSelected: (Int) -> Unit) =
    with(hiltViewModel<ImportsOptionsViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        ScaffoldScreen(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient(),
            topBar = {
                SmallTopBar(
                    navigationIcon = UiIcon.Resource(id = R.drawable.ic_arrow_left),
                    onNavigationClick = onNavigationClick
                )
            }
        ) { innerPaddingValues ->
            ImportsOptionsContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPaddingValues),
                state = state,
                onOptionSelected = { option ->
                    onImportTypeSelected(option.type.ordinal)
                }
            )
        }
    }
