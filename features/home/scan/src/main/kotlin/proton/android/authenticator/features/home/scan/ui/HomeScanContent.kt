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

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import proton.android.authenticator.features.home.scan.presentation.HomeScanState
import proton.android.authenticator.shared.ui.domain.components.camera.CameraQrScan

@Composable
internal fun HomeScanContent(
    state: HomeScanState,
    onPermissionRequested: (Boolean) -> Unit,
    onPermissionRequired: () -> Unit,
    onCloseClick: () -> Unit,
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> onPermissionRequested(isGranted) }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(input = Manifest.permission.CAMERA)
    }

    hasCameraPermission?.let { hasPermission ->
        if (hasPermission) {
            CameraQrScan(
                modifier = modifier,
                onQrCodeScanned = { qrCode, _ -> onQrCodeScanned(qrCode) },
                onCameraError = onCloseClick
            )
        } else {
            onPermissionRequired()
        }
    }
}
