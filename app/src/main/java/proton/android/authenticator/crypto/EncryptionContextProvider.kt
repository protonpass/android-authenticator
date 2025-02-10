package proton.android.authenticator.crypto

interface EncryptionContextProvider {
    fun <R> withEncryptionContext(block: EncryptionContext.() -> R): R
    fun <R> withEncryptionContext(key: EncryptionKey, block: EncryptionContext.() -> R): R
    suspend fun <R> withEncryptionContextSuspendable(block: suspend EncryptionContext.() -> R): R
    suspend fun <R> withEncryptionContextSuspendable(key: EncryptionKey, block: suspend EncryptionContext.() -> R): R
}
