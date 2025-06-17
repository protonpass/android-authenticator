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
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import proton.android.authenticator.shared.ui.domain.analyzers.QrCodeAnalyzer

@Composable
internal fun HomeScanCamera(
    onQrCodeScanned: (String) -> Unit,
    onCameraError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cutoutRect by remember {
        mutableStateOf(Rect.Zero)
    }

    val cameraProvider = remember {
        ProcessCameraProvider.getInstance(context)
            .get()
    }

    val cameraSelector = remember {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    val preview = remember {
        Preview.Builder()
            .build()
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    var previewViewSize by remember {
        mutableStateOf(Size.Zero)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(key1 = lifecycleOwner) {
        onDispose { cameraProvider.unbindAll() }
    }

    if (hasCameraPermission) {
        AndroidView(
            modifier = modifier,
            factory = { factoryContext ->
                val previewView = PreviewView(factoryContext).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                    post {
                        previewViewSize = Size(
                            width = width.toFloat(),
                            height = height.toFloat()
                        )
                    }
                }

                preview.surfaceProvider = previewView.surfaceProvider

                try {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (_: IllegalStateException) {
                    onCameraError()
                } catch (_: IllegalArgumentException) {
                    onCameraError()
                } catch (_: UnsupportedOperationException) {
                    onCameraError()
                }

                previewView
            }
        )

        HomeScanCameraQrMask(cutoutRect = cutoutRect)
    } else {
        val activity = LocalContext.current as? Activity
        HomeScanPermission(
            onOpenAppSettings = {
                try {
                    activity?.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null)
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    AuthenticatorLogger.w(TAG, "Cannot open app settings")
                    AuthenticatorLogger.w(TAG, e)
                }
            }
        )
    }

    LaunchedEffect(previewViewSize) {
        if (previewViewSize == Size.Zero) return@LaunchedEffect

        val cutoutSize = previewViewSize.minDimension * 0.7f
        val left = previewViewSize.width.minus(cutoutSize).div(2)
        val top = previewViewSize.height.minus(cutoutSize).div(3)
        cutoutRect = Rect(
            left = left,
            top = top,
            right = left.plus(cutoutSize),
            bottom = top.plus(cutoutSize)
        )

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            QrCodeAnalyzer(
                onQrCodeScanned = { qrCode ->
                    cameraProvider.unbind(imageAnalysis)
                    onQrCodeScanned(qrCode)
                }
            )
        )
    }
}

private const val TAG = "HomeScanCamera"
