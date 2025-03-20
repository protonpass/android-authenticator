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

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import kotlin.math.roundToInt

class QrCodeAnalyzer(
    private val previewSize: Size,
    private val qrScanArea: Rect,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = setOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    private fun calculateQrScanSource(imageProxy: ImageProxy): LuminanceSource {
        val previewWidth = previewSize.width
        val previewHeight = previewSize.height
        val scanAreaWith = qrScanArea.width
        val scanAreaHeight = qrScanArea.height
        val scanAreaLeft = qrScanArea.left
        val scanAreaTop = qrScanArea.top

        val imageWidth = imageProxy.height
        val imageHeight = imageProxy.width

        val widthRelation = imageWidth / previewWidth
        val heightRelation = imageHeight / previewHeight

        val top = scanAreaTop * heightRelation
        val left = scanAreaLeft * widthRelation
        val width = scanAreaWith * widthRelation
        val height = scanAreaHeight * heightRelation

//        println("JIBIRI: imageWidth: ${imageProxy.width}")
//        println("JIBIRI: imageHeight: ${imageProxy.height}")
//        println("JIBIRI: top: $top")
//        println("JIBIRI: left: $left")
//        println("JIBIRI: width: $width")
//        println("JIBIRI: height: $height")
//        println("JIBIRI: -------------------------------------------")

        return PlanarYUVLuminanceSource(
            imageProxy.planes.first().buffer.toByteArray(),
            imageProxy.width,
            imageProxy.height,
            left.div(2).roundToInt(),
            top.div(2).roundToInt(),
            width.roundToInt(),
            height.roundToInt(),
            false
        )
    }

    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            val source = calculateQrScanSource(image)
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)
                onQrCodeScanned(result.text)
            } catch (e: NotFoundException) {
                println("JIBIRI: No QR code found: ${e.message}")
            } finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}
