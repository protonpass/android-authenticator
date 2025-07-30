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

package proton.android.authenticator.features.unlock.master.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.unlock.master.presentation.UnlockMasterEvent
import proton.android.authenticator.features.unlock.master.presentation.UnlockMasterViewModel
import proton.android.authenticator.shared.ui.domain.components.buttons.PrimaryActionButton
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun UnlockMasterScreen(onUnlockClosed: () -> Unit, onUnlockSucceeded: () -> Unit) =
    with(hiltViewModel<UnlockMasterViewModel>()) {
        val state by stateFlow.collectAsStateWithLifecycle()

        val context = LocalContext.current

        BackHandler(enabled = true) {
            onUnlockClosed()
        }

        LaunchedEffect(key1 = Unit) {
            onRequestBiometricAuthentication(context = context)
        }

        LaunchedEffect(key1 = state.event) {
            when (state.event) {
                UnlockMasterEvent.Idle -> Unit
                UnlockMasterEvent.OnUnlocked -> onUnlockSucceeded()
            }

            onConsumeEvent(event = state.event)
        }

        ScaffoldScreen(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient(),
            bottomBar = {
                PrimaryActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = ThemePadding.Large)
                        .navigationBarsPadding(),
                    text = stringResource(id = uiR.string.action_unlock),
                    onClick = { onRequestBiometricAuthentication(context = context) }
                )
            }
        ) { innerPaddingValues ->
            UnlockMasterContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPaddingValues)
                    .padding(horizontal = ThemePadding.MediumLarge)
            )
        }
    }
