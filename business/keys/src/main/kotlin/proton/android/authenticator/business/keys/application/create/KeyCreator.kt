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

package proton.android.authenticator.business.keys.application.create

import com.proton.gopenpgp.armor.Armor.unarmor
import me.proton.core.crypto.common.context.CryptoContext
import me.proton.core.domain.entity.UserId
import me.proton.core.key.domain.encryptAndSignData
import me.proton.core.user.domain.repository.UserRepository
import proton.android.authenticator.business.keys.domain.KeysApi
import proton.android.authenticator.business.keys.domain.KeysRepository
import proton.android.authenticator.shared.crypto.domain.extensions.tryUseKeys
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class KeyCreator @Inject constructor(
    private val api: KeysApi,
    private val cryptoContext: CryptoContext,
    private val repository: KeysRepository,
    private val userRepository: UserRepository
) {

    @OptIn(ExperimentalEncodingApi::class)
    internal suspend fun create(userId: String) {
        UserId(id = userId)
            .let { sessionUserId ->
                userRepository.getUser(sessionUserId = sessionUserId)
            }
            .let { user ->
                user.tryUseKeys(message = KEY_MESSAGE, cryptoContext) {
                    encryptAndSignData(EncryptionKey.generate().asByteArray())
                }
            }
            .let { encryptedMessage ->
                unarmor(encryptedMessage)
                    .let(Base64::encodeToByteArray)
                    .let(::String)
            }
            .let { encryptedKey ->
                api.create(userId = userId, encryptedKey = encryptedKey)
            }
            .also { key ->
                repository.save(key = key)
            }
    }

    private companion object {

        private const val KEY_MESSAGE = "create authenticator key request"

    }

}
