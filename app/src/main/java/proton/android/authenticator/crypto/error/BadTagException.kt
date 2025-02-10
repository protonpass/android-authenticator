package proton.android.authenticator.crypto.error

class BadTagException(override val message: String, override val cause: Throwable) : RuntimeException(message)
