package proton.android.authenticator.domain

import kotlinx.datetime.Instant
import proton.android.authenticator.commonrust.AuthenticatorEntryModel

data class Entry(
    val id: Int,
    val model: AuthenticatorEntryModel,
    val createdAt: Instant,
    val modifiedAt: Instant
)
