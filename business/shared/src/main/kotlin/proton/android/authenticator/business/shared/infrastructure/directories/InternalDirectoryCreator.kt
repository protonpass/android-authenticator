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

import kotlinx.coroutines.withContext
import proton.android.authenticator.business.shared.di.DirectoryPathInternal
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryCreator
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import java.io.File
import javax.inject.Inject

internal class InternalDirectoryCreator @Inject constructor(
    private val appDispatchers: AppDispatchers,
    @DirectoryPathInternal private val directoryPath: String
) : DirectoryCreator {

    override suspend fun create(directoryName: String) {
        withContext(appDispatchers.io) {
            File(directoryPath, directoryName).also { internalDirectory ->
                if (!internalDirectory.exists()) {
                    internalDirectory.mkdirs()
                }
            }
        }
    }

}
