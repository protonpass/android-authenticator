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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.features.home.scan.presentation.HomeScanState

@Composable
internal fun HomeScanContent(
    state: HomeScanState,
    onCloseClick: () -> Unit,
    onEnterManuallyClick: () -> Unit,
    onQrCodePicked: (Uri) -> Unit,
    onQrCodeScanned: (String) -> Unit
) = with(state) {
    val launcher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        onQrCodePicked(uri)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            HomeScanBottomBar(
                onCloseClick = onCloseClick,
                onEnterManuallyClick = onEnterManuallyClick,
                onOpenGalleryClick = {
                    launcher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }
            )
        }
    ) { paddingValues ->
        HomeScanCamera(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            onQrCodeScanned = onQrCodeScanned,
            onCameraError = onCloseClick
        )
    }
}
