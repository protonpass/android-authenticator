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

package proton.android.authenticator.shared.ui.domain.analyzers

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val qrCodeReader = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
    ).let { hints ->
        MultiFormatReader().apply {
            setHints(hints)
        }
    }

    private val supportedQrCodeImageFormats = setOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.format !in supportedQrCodeImageFormats) return

        val qrCodeSource = calculateQrScanSource(imageProxy)
        val qrCodeBitmap = BinaryBitmap(HybridBinarizer(qrCodeSource))

        try {
            onQrCodeScanned(qrCodeReader.decode(qrCodeBitmap).text)
        } catch (_: NotFoundException) {
            // do nothing when QR code is not found
        } finally {
            imageProxy.close()
        }
    }

    private fun calculateQrScanSource(imageProxy: ImageProxy): LuminanceSource = PlanarYUVLuminanceSource(
        imageProxy.planes.first().buffer.toByteArray(),
        imageProxy.width,
        imageProxy.height,
        0,
        0,
        imageProxy.width,
        imageProxy.height,
        false
    )

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()

        return ByteArray(remaining()).also(::get)
    }

}
