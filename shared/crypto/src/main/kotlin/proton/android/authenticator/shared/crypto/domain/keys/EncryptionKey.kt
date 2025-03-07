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

package proton.android.authenticator.shared.crypto.domain.keys

import java.security.SecureRandom

class EncryptionKey(private val keyBytes: ByteArray) {

    fun clear() {
        keyBytes.indices.map { index ->
            keyBytes[index] = EMPTY_BYTE
        }
    }

    fun clone(): EncryptionKey = EncryptionKey(keyBytes.clone())

    fun asByteArray(): ByteArray {
        if (isEmpty()) throw IllegalStateException("Key has been cleared")

        return keyBytes
    }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is EncryptionKey -> false
        else -> keyBytes.contentEquals(other.keyBytes)
    }

    override fun hashCode(): Int = keyBytes.contentHashCode()

    private fun isEmpty(): Boolean = keyBytes.all { it == EMPTY_BYTE }

    internal companion object {

        private const val KEY_SIZE = 32

        private const val EMPTY_BYTE = 0x00.toByte()

        internal fun generate(): EncryptionKey {
            val buff = ByteArray(KEY_SIZE)
            SecureRandom().nextBytes(buff)
            return EncryptionKey(buff)
        }

    }

}
