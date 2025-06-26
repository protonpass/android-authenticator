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

package proton.android.authenticator.features.imports.onboarding.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.imports.onboarding.presentation.ImportOnboardingEvent
import proton.android.authenticator.features.imports.onboarding.presentation.ImportOnboardingViewModel
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.bars.SmallTopBar
import proton.android.authenticator.shared.ui.domain.components.buttons.PrimaryActionButton
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.R as uiR

@Composable
fun ImportsOnboardingScreen(
    onNavigationClick: () -> Unit,
    onHelpClick: (String) -> Unit,
    onPasswordRequired: (uri: String, importType: Int) -> Unit,
    onCompleted: (Int) -> Unit,
    onError: (Int) -> Unit
) = with(hiltViewModel<ImportOnboardingViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(state.event) {
        when (val event = state.event) {
            ImportOnboardingEvent.Idle -> Unit

            is ImportOnboardingEvent.OnFileImported -> {
                onCompleted(event.importedEntriesCount)
            }

            is ImportOnboardingEvent.OnFileImportFailed -> {
                onError(event.reason)
            }

            is ImportOnboardingEvent.OnFilePasswordRequired -> {
                onPasswordRequired(event.uri, event.importType)
            }
        }

        onEventConsumed(state.event)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            buildList<Uri> {
                result.data?.data?.also(::add)

                result.data?.clipData?.let { clipData ->
                    for (index in 0 until clipData.itemCount) {
                        clipData.getItemAt(index).uri.also(::add)
                    }
                }
            }.also(::onFilesPicked)
        }
    )

    ScaffoldScreen(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        topBar = {
            SmallTopBar(
                navigationIcon = UiIcon.Resource(id = R.drawable.ic_arrow_left),
                onNavigationClick = onNavigationClick
            )
        },
        bottomBar = {
            if (state.isSupported) {
                PrimaryActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ThemePadding.Large,
                            bottom = ThemePadding.Large,
                            end = ThemePadding.Large
                        )
                        .navigationBarsPadding(),
                    text = stringResource(id = uiR.string.action_import),
                    onClick = {
                        Intent(Intent.ACTION_GET_CONTENT)
                            .apply {
                                type = MimeType.All.value
                                addCategory(Intent.CATEGORY_OPENABLE)
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, state.isMultiSelectionAllowed)
                                putExtra(Intent.EXTRA_MIME_TYPES, state.mimeTypes.toTypedArray())
                            }
                            .also(launcher::launch)
                    }
                )
            }
        }
    ) { innerPaddingValues ->
        ImportsOnboardingContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPaddingValues)
                .padding(horizontal = ThemePadding.Large),
            state = state,
            onHelpClick = onHelpClick
        )
    }
}
