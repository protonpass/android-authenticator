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

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.withContext
import proton.android.authenticator.commonrust.QrCodeScannerInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.scanners.QrScanner
import java.io.InputStream
import javax.inject.Inject

internal class RustQrScanner @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val contentResolver: ContentResolver,
    private val qrCodeScanner: QrCodeScannerInterface
) : QrScanner {

    override suspend fun scan(uri: Uri): String? = withContext(appDispatchers.io) {
        contentResolver.openInputStream(uri)
            ?.use(InputStream::readBytes)
            ?.let { imageByteArray ->
                withContext(appDispatchers.default) {
                    qrCodeScanner.scanQrCode(image = imageByteArray)
                }
            }
    }

}
