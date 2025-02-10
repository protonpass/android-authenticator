package proton.android.authenticator.crypto

private const val ENTRY_CONTENT_TAG = "entrycontent"

enum class EncryptionTag(val value: ByteArray) {
    EntryContent(ENTRY_CONTENT_TAG.encodeToByteArray())
}
