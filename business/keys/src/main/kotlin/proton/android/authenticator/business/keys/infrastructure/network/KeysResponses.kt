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

package proton.android.authenticator.business.keys.infrastructure.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class KeyResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Key")
    internal val key: KeyDto
)

@Serializable
internal data class KeysResponseDto(
    @SerialName("Code")
    internal val code: Int,
    @SerialName("Keys")
    internal val keys: KeysDto
)

@Serializable
internal data class KeysDto(
    @SerialName("Keys")
    internal val keys: List<KeyDto>
)

@Serializable
internal data class KeyDto(
    @SerialName("KeyID")
    internal val keyId: String,
    @SerialName("Key")
    internal val key: String,
    @SerialName("UserKeyID")
    internal val userKeyId: String
)
