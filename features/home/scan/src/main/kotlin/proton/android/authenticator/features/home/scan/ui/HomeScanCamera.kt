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
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import kotlin.math.roundToInt

@Composable
internal fun HomeScanCamera(
    onQrCodeScanned: (String) -> Unit,
    onCameraError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cutoutRect by remember { mutableStateOf(Rect(0f, 0f, 0f, 0f)) }

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

    var previewViewSize by remember {
        mutableStateOf(Size.Zero)
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(key1 = lifecycleOwner) {
        onDispose { cameraProvider.unbindAll() }
    }

    if (hasCamPermission) {
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
                        previewViewSize = Size(width = width.toFloat(), height = height.toFloat())
                    }
                }

                preview.surfaceProvider = previewView.surfaceProvider

                try {
                    ProcessCameraProvider.getInstance(factoryContext)
                        .get()
                        .bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                } catch (error: IllegalStateException) {
                    println("JIBIRI: Error creating camera preview -> ${error.message}")
                    onCameraError()
                } catch (error: IllegalArgumentException) {
                    println("JIBIRI: Error creating camera preview -> ${error.message}")
                    onCameraError()
                } catch (error: UnsupportedOperationException) {
                    println("JIBIRI: Error creating camera preview -> ${error.message}")
                    onCameraError()
                }

                previewView
            }
        )
    }

    LaunchedEffect(previewViewSize) {
        if (previewViewSize != Size.Zero) {
            val cutoutSize = previewViewSize.minDimension * 0.7f // 70% of the smallest dimension
            val left = (previewViewSize.width - cutoutSize) / 2
            val top = (previewViewSize.height - cutoutSize) / 2
            cutoutRect = Rect(
                left,
                top,
                left + cutoutSize.roundToInt(),
                top + cutoutSize.roundToInt()
            )

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer(
                    previewSize = previewViewSize,
                    qrScanArea = cutoutRect,
                    onQrCodeScanned = onQrCodeScanned
                )
            )
        }
    }

    Box {
        DrawCutoutOverlay(cutoutRect = cutoutRect)

        Text(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = cutoutRect.bottom.roundToInt()
                    )
                }
                .fillMaxWidth()
                .padding(top = ThemePadding.Large),
            text = "Point your camera at the QR code",
            color = Theme.colorScheme.textNorm,
            style = Theme.typography.headline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DrawCutoutOverlay(cutoutRect: Rect) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val path = Path().apply {
            fillType = PathFillType.EvenOdd

            // Add the outer rectangle (the entire canvas)
            addRect(
                Rect(
                    Offset(x = 0f, 0f),
                    Size(canvasWidth, canvasHeight)
                )
            )

            val roundRectPadding = ThemePadding.ExtraSmall

            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(
                            x = cutoutRect.left + roundRectPadding.toPx().times(2),
                            y = cutoutRect.top + roundRectPadding.toPx().times(2)
                        ),
                        size = Size(
                            cutoutRect.width - roundRectPadding.toPx().times(4),
                            cutoutRect.height - roundRectPadding.toPx().times(4)
                        )
                    ),
                    cornerRadius = CornerRadius(x = ThemeRadius.ExtraSmall.toPx())
                )
            )
        }

        drawPath(
            path = path,
            color = Color.Black.copy(alpha = 0.6f)
        )

        val dashedRectPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(cutoutRect.left.toFloat(), cutoutRect.top.toFloat()),
                        size = Size(
                            cutoutRect.width.toFloat(),
                            cutoutRect.height.toFloat()
                        )
                    ),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            )
        }

        val lineInterval = cutoutRect.width.toFloat().div(2)
        val gapInterval = cutoutRect.width.toFloat().div(2) - ThemeRadius.ExtraSmall.toPx()
        val phase = cutoutRect.width.toFloat().div(4) + ThemeRadius.ExtraSmall.toPx().times(1.5f)

        drawPath(
            path = dashedRectPath,
            color = Color.White,
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(lineInterval, gapInterval),
                    phase = phase
                )
            )
        )
    }
}
