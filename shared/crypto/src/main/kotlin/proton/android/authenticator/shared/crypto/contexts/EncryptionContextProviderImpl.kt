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

package proton.android.authenticator.shared.crypto.contexts

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import me.proton.core.crypto.common.keystore.KeyStoreCrypto
import me.proton.core.crypto.common.keystore.PlainByteArray
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContext
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.keys.EncryptionKey
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.inject.Inject
import kotlin.concurrent.withLock
import kotlin.experimental.xor

internal class EncryptionContextProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keyStoreCrypto: KeyStoreCrypto,
    private val appDispatchers: AppDispatchers
) : EncryptionContextProvider {

    private val lock = ReentrantReadWriteLock()

    private var storedKey: ByteArray? = null

    override suspend fun <R> withEncryptionContext(
        encryptionKey: EncryptionKey?,
        block: suspend EncryptionContext.() -> R
    ): R = withContext(appDispatchers.default) {
        val key = encryptionKey ?: getKey()
        val context = EncryptionContext(key)

        try {
            val res = block(context)
            return@withContext res
        } finally {
            key.clear()
        }
    }

    private suspend fun getKey(): EncryptionKey = withContext(appDispatchers.io) {
        // Try to get it from the stored value
        val readLock = lock.readLock()
        readLock.withLock {
            storedKey?.let { stored ->
                val deobfuscated = deobfuscateKey(stored)
                return@withContext EncryptionKey(deobfuscated)
            }
        }

        // It was not stored. Try to get it from the file or generate it
        val writeLock = lock.writeLock()
        writeLock.withLock {
            // Check again if it's stored to see if other thread already stored it
            storedKey?.let { stored ->
                val deobfuscated = deobfuscateKey(stored)
                return@withLock EncryptionKey(deobfuscated)
            }

            // Guaranteed it's not stored. Read it or generate it
            val file = File(context.dataDir, KEY_FILE_NAME)
            val key = if (file.exists()) {
                val encryptedKey = file.readBytes()
                val decryptedKey = keyStoreCrypto.decrypt(EncryptedByteArray(encryptedKey))
                EncryptionKey(decryptedKey.array)
            } else {
                generateKey(file)
            }

            // Store the key obfuscated in memory. We can do it as we are in the writeLock context
            storedKey = obfuscateKey(key.asByteArray())

            // Return the key to be used
            key
        }
    }

    private fun obfuscateKey(input: ByteArray): ByteArray {
        val obfuscated = ByteArray(input.size + 2)
        for (i in input.indices) {
            obfuscated[i] = input[i] xor XOR_KEY
        }
        obfuscated[input.size] = XOR_KEY
        obfuscated[input.size + 1] = XOR_KEY

        return obfuscated
    }

    private fun deobfuscateKey(input: ByteArray): ByteArray {
        if (input[input.size - 1] != XOR_KEY || input[input.size - 2] != XOR_KEY) {
            throw IllegalStateException("Invalid obfuscated key")
        }

        val deobfuscated = ByteArray(input.size - 2)
        for (i in deobfuscated.indices) {
            deobfuscated[i] = input[i] xor XOR_KEY
        }

        return deobfuscated
    }

    private fun generateKey(file: File): EncryptionKey {
        val key = EncryptionKey.generate()
        val encrypted = keyStoreCrypto.encrypt(PlainByteArray(key.asByteArray()))
        file.writeBytes(encrypted.array)

        return key
    }

    private companion object {

        private const val KEY_FILE_NAME = "authenticator.key"

        private const val XOR_KEY = 0xDE.toByte()

    }

}
