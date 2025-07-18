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

import android.util.Log
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaInstant
import me.proton.core.presentation.app.AppLifecycleProvider
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.logs.LogsFileProvider
import proton.android.authenticator.shared.common.domain.logs.LogsFileTreeProvider
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

internal class AuthenticatorLogsFileTreeProvider @Inject constructor(
    appLifecycleProvider: AppLifecycleProvider,
    private val appDispatchers: AppDispatchers,
    private val logsFileProvider: LogsFileProvider,
    private val timeProvider: TimeProvider
) : Timber.Tree(), LogsFileTreeProvider {

    private val coroutineScope = appLifecycleProvider.lifecycle.coroutineScope

    private val dateTimeFormatter = DateTimeFormatter
        .ofPattern(LOG_TIME_FORMAT, Locale.getDefault())
        .withZone(ZoneId.from(ZoneOffset.UTC))

    init {
        handleLogsRotation()
    }

    private fun handleLogsRotation() {
        coroutineScope.launch(appDispatchers.io) {
            try {
                logsFileProvider.provide()
                    .takeIf { logsFile -> logsFile.exists() && logsFile.length() >= LOG_FILE_MAX_SIZE }
                    ?.let { logsFile ->
                        logsFile to File.createTempFile(LOG_TEMP_FILE, null, logsFile.parentFile)
                    }
                    ?.also { (logsFile, tempLogsFile) ->
                        logsFile.copyTo(tempLogsFile, overwrite = true)
                    }
                    ?.also { (logsFile, tempLogsFile) ->
                        tempLogsFile.bufferedReader().use { tempReader ->
                            var lines = tempReader.lines().count()

                            logsFile.bufferedWriter().use { logsWriter ->
                                tempReader.forEachLine { tempLogLine ->
                                    if (lines < LOG_ROTATION_MAX_LINES) {
                                        logsWriter.appendLine(tempLogLine)
                                    }
                                    lines--
                                }
                                logsWriter.flush()
                            }
                        }
                    }
                    ?.also { (_, tempLogsFile) ->
                        tempLogsFile.delete()
                    }
            } catch (error: FileNotFoundException) {
                AuthenticatorLogger.w(TAG, "Could not rotate logs file: file not found")
                AuthenticatorLogger.w(TAG, error)
            } catch (error: IOException) {
                AuthenticatorLogger.w(TAG, "Could not rotate logs file: IO operation error")
                AuthenticatorLogger.w(TAG, error)
            }
        }
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority < Log.INFO) return

        coroutineScope.launch(appDispatchers.io) {
            logsFileProvider.provide()
                .takeIf { logsFile -> logsFile.exists() }
                ?.let { logsFile -> FileWriter(logsFile, true) }
                ?.let(::BufferedWriter)
                ?.also { bufferedWriter ->
                    try {
                        bufferedWriter.use { writer ->
                            createLogEntry(priority, tag, message).also(writer::write)
                            writer.newLine()
                            writer.flush()
                        }
                    } catch (error: IOException) {
                        AuthenticatorLogger.w(TAG, "Could not write to logs file")
                        AuthenticatorLogger.w(TAG, error)
                    }
                }
        }
    }

    private fun createLogEntry(
        priority: Int,
        tag: String?,
        message: String
    ): String = timeProvider.currentInstant()
        .toJavaInstant()
        .let(dateTimeFormatter::format)
        .let { logTime ->
            "$logTime ${priority.priorityChar()}: ${tag ?: LOG_TAG_DEFAULT} - $message"
        }

    private fun Int.priorityChar(): Char = when (this) {
        Log.ASSERT -> 'A'
        Log.DEBUG -> 'D'
        Log.ERROR -> 'E'
        Log.INFO -> 'I'
        Log.VERBOSE -> 'V'
        Log.WARN -> 'W'
        else -> '-'
    }

    override fun provide(): Timber.Tree = this

    private companion object {

        private const val TAG = "AuthenticatorLogsFileTreeProvider"

        private const val LOG_TAG_DEFAULT = "EmptyTag"

        private const val LOG_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS"

        private const val LOG_FILE_MAX_SIZE: Long = 4 * 1024 * 1024 // 4 MB

        private const val LOG_ROTATION_MAX_LINES = 500

        private const val LOG_TEMP_FILE = "temp.log"

    }

}
