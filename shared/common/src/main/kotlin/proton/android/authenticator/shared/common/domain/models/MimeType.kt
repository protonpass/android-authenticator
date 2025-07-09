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

package proton.android.authenticator.shared.common.domain.models

enum class MimeType(val value: String) {
    All(value = "*/*"),
    Binary(value = "application/octet-stream"),
    CommaSeparatedValues(value = "text/comma-separated-values"),
    Csv(value = "text/csv"),
    Image(value = "image/*"),
    Json(value = "application/json"),
    Text(value = "text/plain"),
    Zip(value = "application/zip");

    companion object {

        fun fromValue(value: String): MimeType = when (value) {
            All.value -> All
            Binary.value -> Binary
            CommaSeparatedValues.value -> CommaSeparatedValues
            Csv.value -> Csv
            Json.value -> Json
            Text.value -> Text
            Zip.value -> Zip
            else -> throw IllegalArgumentException("Unknown MimeType value: $value")
        }

    }

}
