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

package proton.android.authenticator.shared.ui.domain.components.camera

import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import proton.android.authenticator.shared.ui.domain.analyzers.QrCodeAnalyzer
import proton.android.authenticator.shared.ui.domain.theme.LocalIsScreenshotTest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val FOCUS_AUTO_CANCEL_SECONDS = 3L
private const val FOCUS_RETRY_DELAY_MILLIS = 1500L

@Composable
fun CameraQrScan(
    onQrCodeScanned: (String, ByteArray) -> Unit,
    onCameraError: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current || LocalIsScreenshotTest.current) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "This is a camera", color = Color.White)
        }
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cutoutRect by remember {
        mutableStateOf(Rect.Zero)
    }

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    var previewView by remember {
        mutableStateOf<PreviewView?>(null)
    }

    val cameraSelector = remember {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    val mainExecutor = remember(context) {
        ContextCompat.getMainExecutor(context)
    }

    LaunchedEffect(Unit) {
        runCatching {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                runCatching {
                    cameraProvider = cameraProviderFuture.get()
                }.onFailure { _: Throwable ->
                    onCameraError()
                }
            }, mainExecutor)
        }.onFailure { _: Throwable ->
            onCameraError()
        }
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

    var canScanCode by remember {
        mutableStateOf(true)
    }

    val qrAnalysisExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> true
                Lifecycle.Event.ON_PAUSE -> false
                Lifecycle.Event.ON_CREATE,
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_STOP,
                Lifecycle.Event.ON_DESTROY,
                Lifecycle.Event.ON_ANY -> null
            }?.also { isScanAllowed -> canScanCode = isScanAllowed }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

            imageAnalysis.clearAnalyzer()
            qrAnalysisExecutor.shutdown()
            camera = null
            previewView = null
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(cameraProvider) {
        val provider = cameraProvider ?: return@LaunchedEffect

        runCatching {
            provider.unbindAll()
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }.onFailure { _: Throwable ->
            onCameraError()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            PreviewView(factoryContext).apply {
                previewView = this
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

                preview.surfaceProvider = surfaceProvider
            }
        },
        update = { }
    )

    CameraQrScanMask(cutoutRect = cutoutRect)

    LaunchedEffect(key1 = previewViewSize) {
        if (previewViewSize == Size.Zero) return@LaunchedEffect

        val cutoutSize = previewViewSize.minDimension * QrCodeAnalyzer.SCAN_WINDOW_SIZE_RATIO
        val left = previewViewSize.width.minus(cutoutSize).div(2)
        val top = previewViewSize.height.minus(cutoutSize).div(3)
        cutoutRect = Rect(
            left = left,
            top = top,
            right = left.plus(cutoutSize),
            bottom = top.plus(cutoutSize)
        )

        imageAnalysis.setAnalyzer(
            qrAnalysisExecutor,
            QrCodeAnalyzer(
                previewWidthProvider = { previewViewSize.width },
                previewHeightProvider = { previewViewSize.height },
                cutoutRectProvider = {
                    android.graphics.Rect(
                        cutoutRect.left.toInt(),
                        cutoutRect.top.toInt(),
                        cutoutRect.right.toInt(),
                        cutoutRect.bottom.toInt()
                    )
                },
                onQrCodeScanned = { qrCodeValue, qrCodeBytes ->
                    mainExecutor.execute {
                        if (canScanCode) {
                            canScanCode = false

                            onQrCodeScanned(qrCodeValue, qrCodeBytes)
                        }
                    }
                }
            )
        )
    }

    LaunchedEffect(camera, previewView, cutoutRect, canScanCode) {
        val currentCamera = camera ?: return@LaunchedEffect
        val currentPreviewView = previewView ?: return@LaunchedEffect
        val focusPoint = calculateFocusPoint(cutoutRect) ?: return@LaunchedEffect

        while (canScanCode) {
            runCatching {
                val meteringPoint = currentPreviewView.meteringPointFactory.createPoint(focusPoint.x, focusPoint.y)
                val focusAction = FocusMeteringAction.Builder(
                    meteringPoint,
                    FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                ).setAutoCancelDuration(FOCUS_AUTO_CANCEL_SECONDS, TimeUnit.SECONDS).build()
                currentCamera.cameraControl.startFocusAndMetering(focusAction)
            }

            delay(FOCUS_RETRY_DELAY_MILLIS)
        }
    }
}

internal fun calculateFocusPoint(cutoutRect: Rect): FocusPoint? {
    if (cutoutRect == Rect.Zero) return null

    return FocusPoint(
        x = cutoutRect.center.x,
        y = cutoutRect.center.y
    )
}

internal data class FocusPoint(
    val x: Float,
    val y: Float
)
