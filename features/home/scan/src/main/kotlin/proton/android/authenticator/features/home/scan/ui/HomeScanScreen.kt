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

package proton.android.authenticator.features.home.scan.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import proton.android.authenticator.features.home.scan.presentation.HomeScanEvent
import proton.android.authenticator.features.home.scan.presentation.HomeScanViewModel
import proton.android.authenticator.shared.ui.domain.screens.ScaffoldScreen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Composable
fun HomeScanScreen(
    onCloseClick: () -> Unit,
    onManualEntryClick: () -> Unit,
    onCreateEntryError: () -> Unit,
    onCreateEntrySuccess: () -> Unit,
    onPermissionRequired: () -> Unit
) = with(hiltViewModel<HomeScanViewModel>()) {
    val state by stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.event) {
        when (state.event) {
            HomeScanEvent.Idle -> Unit
            HomeScanEvent.OnEntryCreationFailed -> onCreateEntryError()
            HomeScanEvent.OnEntryCreationSucceeded -> onCreateEntrySuccess()
        }

        onConsumeEvent(event = state.event)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = ::onScanEntryQr
    )

    ScaffoldScreen(
        bottomBar = {
            if (state.showBottomBar) {
                HomeScanBottomBar(
                    modifier = Modifier
                        .imePadding()
                        .systemBarsPadding()
                        .fillMaxWidth()
                        .padding(
                            start = ThemePadding.Medium,
                            end = ThemePadding.Medium,
                            bottom = ThemePadding.Large
                        ),
                    onCloseClick = onCloseClick,
                    onEnterManuallyClick = onManualEntryClick,
                    onOpenGalleryClick = {
                        launcher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    }
                )
            }
        }
    ) {
        HomeScanContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onCloseClick = onCloseClick,
            onPermissionRequested = ::onCameraPermissionRequested,
            onPermissionRequired = onPermissionRequired,
            onQrCodeScanned = ::onCreateEntry
        )
    }
}
