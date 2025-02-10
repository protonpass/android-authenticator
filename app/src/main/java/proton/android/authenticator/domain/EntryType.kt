package proton.android.authenticator.domain

private const val TOTP_KEY = 1
private const val STEAM_KEY = 2

enum class EntryType(val code: Int) {
    TOTP(TOTP_KEY),
    STEAM(STEAM_KEY);

    companion object {
        fun fromCode(code: Int): EntryType? = entries.find { it.code == code }
    }
}
