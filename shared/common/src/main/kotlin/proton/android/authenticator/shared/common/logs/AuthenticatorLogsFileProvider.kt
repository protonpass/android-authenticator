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

package proton.android.authenticator.shared.common.logs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.logs.LogsFileProvider
import java.io.File
import javax.inject.Inject

internal class AuthenticatorLogsFileProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDispatchers: AppDispatchers
) : LogsFileProvider {

    override suspend fun provide(): File = withContext(appDispatchers.io) {
        File(context.cacheDir, LOG_DIRECTORY_NAME)
            .also { logsDirectory ->
                if (!logsDirectory.exists()) {
                    logsDirectory.mkdirs()
                }
            }
            .let { logsDirectory ->
                File(logsDirectory, LOG_FILE_NAME)
            }
            .also { logsFile ->
                if (!logsFile.exists()) {
                    logsFile.createNewFile()
                }
            }
    }

    private companion object {

        private const val LOG_DIRECTORY_NAME = "logs"

        private const val LOG_FILE_NAME = "authenticator.log"

    }

}
