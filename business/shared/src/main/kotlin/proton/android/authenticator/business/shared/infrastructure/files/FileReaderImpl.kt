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

package proton.android.authenticator.business.shared.infrastructure.files

import android.content.ContentResolver
import androidx.core.net.toUri
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.shared.domain.errors.FileTooLargeException
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileReader
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal class FileReaderImpl @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val contentResolver: ContentResolver
) : FileReader {

    override suspend fun readText(path: String): String = withContext(appDispatchers.io) {
        path.toUri().let { pathUri ->
            contentResolver.openInputStream(pathUri)
                ?.bufferedReader()
                ?.use(BufferedReader::readText)
                .orEmpty()
        }
    }
    override suspend fun readBinary(path: String, maxSize: Int): ByteArray? {
        path.toUri().let { pathUri ->
            return contentResolver.openInputStream(pathUri)?.use { inputStream ->
                if (inputStream.available() > maxSize) throw FileTooLargeException(maxSize)
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1_024)
                var bytesRead: Int
                while (true) {
                    bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) {
                        break
                    }
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.toByteArray()
            }
        }
    }


}
