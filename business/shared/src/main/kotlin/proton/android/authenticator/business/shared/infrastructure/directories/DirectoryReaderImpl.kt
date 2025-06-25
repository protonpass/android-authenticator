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

package proton.android.authenticator.business.shared.infrastructure.directories

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryReader
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import javax.inject.Inject

internal class DirectoryReaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDispatchers: AppDispatchers
) : DirectoryReader {

    override suspend fun read(uri: Uri): List<DocumentFile> = withContext(appDispatchers.io) {
        val documentDir = DocumentFile.fromTreeUri(context, uri)

        if (documentDir?.exists() == true && documentDir.isDirectory) {
            documentDir.listFiles().toList()
        } else {
            emptyList()
        }
    }
}
