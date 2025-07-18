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

package proton.android.authenticator.business.logs.infrastructure

import android.content.Context
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.logs.domain.Logs
import proton.android.authenticator.business.logs.domain.LogsRepository
import proton.android.authenticator.shared.common.domain.configs.AppConfig
import proton.android.authenticator.shared.common.domain.constants.CharacterConstants
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.logs.LogsFileProvider
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import java.io.File
import java.io.IOException
import javax.inject.Inject

internal class CacheFileLogsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appConfig: AppConfig,
    private val appDispatchers: AppDispatchers,
    private val logsFileProvider: LogsFileProvider
) : LogsRepository {

    override fun find(): Flow<Logs?> = flow {
        try {
            logsFileProvider.provide()
                .takeIf { logsFile -> logsFile.exists() }
                ?.let { logsFile ->
                    logsFile to withContext(appDispatchers.io) {
                        logsFile.bufferedReader().use { reader ->
                            reader.readLines()
                                .takeLast(LOGS_TAIL_SIZE)
                                .reversed()
                                .joinToString(separator = CharacterConstants.NEW_LINE)
                        }
                    }
                }
                ?.let { (logsFile, logsContent) ->
                    Logs(
                        content = logsContent,
                        fileUri = createLogsFileAuthorizedUri(logsFile)
                    )
                }
                ?.also { logs -> emit(logs) }
                ?: emit(null)
        } catch (error: IOException) {
            AuthenticatorLogger.w(TAG, "Cannot read logs file")
            AuthenticatorLogger.w(TAG, error)

            emit(null)
        }
    }

    private fun createLogsFileAuthorizedUri(logsFile: File) = FileProvider.getUriForFile(
        context,
        "${appConfig.applicationId}.fileprovider",
        logsFile
    )

    private companion object {

        private const val TAG = "CacheFileLogsRepository"

        private const val LOGS_TAIL_SIZE = 100

    }

}
