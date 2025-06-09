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

package proton.android.authenticator.shared.ui.domain.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import proton.android.authenticator.shared.ui.R
import java.time.format.DateTimeFormatter
import java.util.Locale

@Stable
sealed interface UiDate {

    @Composable
    fun asString(): String

    @Stable
    data class Backup(
        private val timestampMillis: Long,
        private val locale: Locale = Locale.getDefault()
    ) : UiDate {

        @Composable
        override fun asString(): String {
            val timeZone: TimeZone = TimeZone.currentSystemDefault()
            val dateTime = Instant.fromEpochMilliseconds(timestampMillis).toLocalDateTime(timeZone)
            val nowDateTime = Clock.System.now().toLocalDateTime(timeZone)
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')

            return when (dateTime.date) {
                nowDateTime.date -> {
                    stringResource(id = R.string.date_format_today, "$hour:$minute")
                }

                nowDateTime.date.minus(1, DateTimeUnit.DAY) -> {
                    stringResource(id = R.string.date_format_yesterday, "$hour:$minute")
                }

                else -> {
                    stringResource(id = R.string.date_format_full_compact_month).let { pattern ->
                        DateTimeFormatter.ofPattern(pattern)
                            .withLocale(locale)
                            .format(dateTime.toJavaLocalDateTime())
                    }
                }
            }
        }

    }

}
