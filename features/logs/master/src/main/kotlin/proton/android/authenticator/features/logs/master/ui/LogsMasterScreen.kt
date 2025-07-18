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

package proton.android.authenticator.features.logs.master.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.logs.master.R
import proton.android.authenticator.features.logs.master.presentation.LogsMasterViewModel
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun LogsMasterScreen(onNavigationClick: () -> Unit, onShareLogsClick: (Uri) -> Unit) =
    with(hiltViewModel<LogsMasterViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        val scrollState = rememberScrollState()

        val isBlurred by remember {
            derivedStateOf { scrollState.value > 0 }
        }

        ScaffoldScreen(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient(),
            topBar = {
                SmallTopBar(
                    title = UiText.Resource(id = R.string.logs_screen_title),
                    navigationIcon = UiIcon.Resource(id = uiR.drawable.ic_arrow_left),
                    isBlurred = isBlurred,
                    onNavigationClick = onNavigationClick
                )
            },
            bottomBar = {
                if (state.showBottomBar) {
                    LogsMasterBottomBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Theme.colorScheme.backgroundGradientBottom.copy(alpha = 0.97f))
                            .padding(
                                horizontal = ThemePadding.MediumLarge,
                                vertical = ThemePadding.Medium
                            )
                            .navigationBarsPadding(),
                        onShareClick = { onShareLogsClick(state.logsFileUri) }
                    )
                }
            }
        ) { innerPaddingValues ->
            LogsMasterContent(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
                    .padding(paddingValues = innerPaddingValues)
                    .padding(horizontal = ThemePadding.Medium),
                state = state
            )
        }
    }
