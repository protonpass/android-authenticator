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

package proton.android.authenticator.business.entries.infrastructure.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateEntryResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Entry")
    internal val entry: EntryDto
)

@Serializable
internal data class CreateEntriesResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Entries")
    internal val entries: List<EntryDto>
)

@Serializable
internal data class FetchEntriesResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Entries")
    internal val fetchEntriesDto: FetchEntriesDto
)

@Serializable
internal data class UpdateEntryResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Entry")
    internal val entry: EntryDto
)

@Serializable
internal data class FetchEntriesDto(
    @SerialName("Entries")
    internal val entries: List<EntryDto>,
    @SerialName("Total")
    internal val total: Int,
    @SerialName("LastID")
    internal val lastId: String?
)

@Serializable
internal data class EntryDto(
    @SerialName("EntryID")
    internal val entryId: String,
    @SerialName("AuthenticatorKeyID")
    internal val keyId: String,
    @SerialName("Revision")
    internal val revision: Int,
    @SerialName("ContentFormatVersion")
    internal val contentFormatVersion: Int,
    @SerialName("Content")
    internal val content: String,
    @SerialName("Flags")
    internal val flags: Int,
    @SerialName("CreateTime")
    internal val createTime: Long,
    @SerialName("ModifyTime")
    internal val modifyTime: Long
)
