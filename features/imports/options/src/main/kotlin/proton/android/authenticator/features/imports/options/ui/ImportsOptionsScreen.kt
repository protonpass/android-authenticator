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

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.imports.options.presentation.ImportsOptionsEvent
import proton.android.authenticator.features.imports.options.presentation.ImportsOptionsViewModel
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.ui.domain.screens.BottomSheetScreen

@Composable
fun ImportsOptionsScreen(
    onPasswordRequired: (uri: String, importType: Int) -> Unit,
    onCompleted: (Int) -> Unit,
    onDismissed: () -> Unit
) = with(hiltViewModel<ImportsOptionsViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onFilePicked(uri = result.data?.data, importType = state.selectedOptionModel?.type)
    }

    LaunchedEffect(state.event) {
        when (val event = state.event) {
            ImportsOptionsEvent.Idle -> Unit
            is ImportsOptionsEvent.OnChooseFile -> {
                Intent(Intent.ACTION_GET_CONTENT)
                    .apply {
                        type = MimeType.All.value
                        addCategory(Intent.CATEGORY_OPENABLE)
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                        putExtra(Intent.EXTRA_MIME_TYPES, event.mimeTypes.toTypedArray())
                    }
                    .also(launcher::launch)
            }

            is ImportsOptionsEvent.OnFileImported -> {
                onCompleted(event.importedEntriesCount)
            }

            is ImportsOptionsEvent.OnFilePasswordRequired -> {
                onPasswordRequired(event.uri, event.importType)
            }
        }

        onEventConsumed(state.event)
    }

    BottomSheetScreen(
        onDismissed = onDismissed
    ) {
        ImportsOptionsContent(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            onOptionSelected = ::onOptionSelected
        )
    }
}
