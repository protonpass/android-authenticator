package proton.android.authenticator.crypto

import me.proton.core.crypto.common.keystore.EncryptedByteArray

interface EncryptionContext {
    fun encrypt(content: ByteArray, encryptionTag: EncryptionTag): EncryptedByteArray

    fun decrypt(content: EncryptedByteArray, encryptionTag: EncryptionTag): ByteArray
}
