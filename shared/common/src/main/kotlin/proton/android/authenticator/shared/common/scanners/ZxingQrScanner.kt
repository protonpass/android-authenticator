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

package proton.android.authenticator.shared.common.scanners

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.scanners.QrScanner
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

internal class ZxingQrScanner @Inject constructor(
    private val appDispatchers: AppDispatchers,
    @ApplicationContext private val context: Context
) : QrScanner {

    private val reader = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
    ).let { hints ->
        MultiFormatReader().apply {
            setHints(hints)
        }
    }

    override suspend fun scan(uri: Uri, maxDimension: Int): String? = withContext(appDispatchers.default) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src) { decoder, info, _ ->
                    if (info.size.width == 0 || info.size.height == 0) {
                        throw NotFoundException.getNotFoundInstance()
                    }
                    decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                    val rawScale = min(
                        maxDimension.toFloat() / info.size.width,
                        maxDimension.toFloat() / info.size.height
                    )
                    val scale = min(1f, rawScale)
                    decoder.setTargetSize(
                        (info.size.width * scale).toInt(),
                        (info.size.height * scale).toInt()
                    )
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }.let { bitmap ->
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                reader.decode(binaryBitmap).text
            }
        } catch (_: NotFoundException) {
            null
        } catch (_: FileNotFoundException) {
            null
        } catch (_: IOException) {
            null
        }
    }

}
