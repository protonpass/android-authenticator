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

package proton.android.authenticator.business.keys.infrastructure

import me.proton.core.domain.entity.UserId
import me.proton.core.network.data.ApiProvider
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.keys.domain.KeysApi
import proton.android.authenticator.business.keys.infrastructure.network.CreateKeyRequestDto
import proton.android.authenticator.business.keys.infrastructure.network.KeyDto
import proton.android.authenticator.business.keys.infrastructure.network.retrofit.RetrofitKeysDataSource
import javax.inject.Inject

internal class KeysApiImpl @Inject constructor(private val apiProvider: ApiProvider) : KeysApi {

    override suspend fun create(userId: String, encryptedKey: String): Key = apiProvider
        .get<RetrofitKeysDataSource>(userId = UserId(id = userId))
        .invoke { createKey(request = CreateKeyRequestDto(key = encryptedKey)) }
        .valueOrThrow
        .key
        .toDomain()

    override suspend fun fetchAll(userId: String): List<Key> = apiProvider
        .get<RetrofitKeysDataSource>(userId = UserId(id = userId))
        .invoke { getKeys() }
        .valueOrThrow
        .keys
        .keys
        .map(KeyDto::toDomain)

}

private fun KeyDto.toDomain() = Key(
    id = keyId,
    key = key,
    userKeyId = userKeyId
)
