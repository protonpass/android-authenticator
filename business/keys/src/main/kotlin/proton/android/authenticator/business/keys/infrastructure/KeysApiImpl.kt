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

import kotlinx.coroutines.withContext
import me.proton.core.crypto.common.context.CryptoContext
import me.proton.core.domain.entity.UserId
import me.proton.core.key.domain.decryptAndVerifyData
import me.proton.core.key.domain.getArmored
import me.proton.core.network.data.ApiProvider
import me.proton.core.user.domain.repository.UserRepository
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.keys.domain.KeysApi
import proton.android.authenticator.business.keys.infrastructure.network.CreateKeyRequestDto
import proton.android.authenticator.business.keys.infrastructure.network.KeyDto
import proton.android.authenticator.business.keys.infrastructure.network.retrofit.RetrofitKeysDataSource
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.extensions.tryUseKeys
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class KeysApiImpl @Inject constructor(
    private val apiProvider: ApiProvider,
    private val appDispatchers: AppDispatchers,
    private val cryptoContext: CryptoContext,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val userRepository: UserRepository
) : KeysApi {

    override suspend fun create(userId: String, encryptedKey: String): Key = apiProvider
        .get<RetrofitKeysDataSource>(userId = UserId(id = userId))
        .invoke { createKey(request = CreateKeyRequestDto(key = encryptedKey)) }
        .valueOrThrow
        .key
        .toDomain(userId = userId)

    override suspend fun fetchAll(userId: String): List<Key> = apiProvider
        .get<RetrofitKeysDataSource>(userId = UserId(id = userId))
        .invoke { getKeys() }
        .valueOrThrow
        .keys
        .keys
        .map { keyDto -> keyDto.toDomain(userId = userId) }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun KeyDto.toDomain(userId: String) = UserId(id = userId)
        .let { sessionUserId ->
            userRepository.getUser(sessionUserId = sessionUserId)
        }
        .let { user ->
            withContext(appDispatchers.default) {
                key
                    .let(Base64::decode)
                    .let { decodedKey ->
                        user.tryUseKeys(message = KEY_MESSAGE, cryptoContext) {
                            decryptAndVerifyData(getArmored(decodedKey))
                        }
                    }
                    .data
            }
        }
        .let { decryptedKey ->
            encryptionContextProvider.withEncryptionContext {
                encrypt(decryptedKey)
            }
        }
        .let { encryptedKey ->
            Key(
                id = keyId,
                key = key,
                userKeyId = userKeyId,
                encryptedKey = encryptedKey
            )
        }

    private companion object {

        private const val KEY_MESSAGE = "reencrypt authenticator key request"

    }

}
