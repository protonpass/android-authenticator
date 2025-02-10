/*
 * Copyright (c) 2023 Proton AG
 * This file is part of Proton AG and Proton Pass.
 *
 * Proton Pass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Pass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Pass.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.authenticator.crypto

import me.proton.core.crypto.common.keystore.EncryptedByteArray
import proton.android.authenticator.crypto.error.BadTagException
import java.lang.System.arraycopy
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val ENTRY_CONTENT_TAG = "entrycontent"

class EncryptionContextImpl(key: EncryptionKey) : EncryptionContext {

    private val secretKeySpec = SecretKeySpec(key.value(), ALGORITHM)

    override fun encrypt(content: ByteArray): EncryptedByteArray {
        val cipher = cipherFactory()
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

        cipher.updateAAD(ENTRY_CONTENT_TAG.encodeToByteArray())

        val cipherByteArray = cipher.doFinal(content)
        val result = ByteArray(IV_SIZE + cipherByteArray.size)
        arraycopy(cipher.iv, 0, result, 0, IV_SIZE)
        arraycopy(cipherByteArray, 0, result, IV_SIZE, cipherByteArray.size)
        return EncryptedByteArray(result)
    }

    override fun decrypt(content: EncryptedByteArray): ByteArray {
        val cipher = cipherFactory()

        val iv = content.array.copyOfRange(0, IV_SIZE)
        val cipherByteArray = content.array.copyOfRange(IV_SIZE, content.array.size)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, GCMParameterSpec(CIPHER_GCM_TAG_BITS, iv))

        cipher.updateAAD(ENTRY_CONTENT_TAG.encodeToByteArray())
        try {
            return cipher.doFinal(cipherByteArray)
        } catch (e: AEADBadTagException) {
            val tagName = ENTRY_CONTENT_TAG
            throw BadTagException("Bad AEAD Tag when decoding content [tag=$tagName]", e)
        }
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val CIPHER_GCM_TAG_BITS = 128

        private fun cipherFactory(): Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
    }
}
