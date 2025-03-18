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

package proton.android.authenticator.business.entries.domain

import proton.android.authenticator.commonrust.AuthenticatorTotpAlgorithm

enum class EntryAlgorithm(val value: Int) {
    SHA1(value = 0),
    SHA256(value = 1),
    SHA512(value = 2);

    internal fun asAuthenticatorEntryAlgorithm(): AuthenticatorTotpAlgorithm = when (this) {
        SHA1 -> AuthenticatorTotpAlgorithm.SHA1
        SHA256 -> AuthenticatorTotpAlgorithm.SHA256
        SHA512 -> AuthenticatorTotpAlgorithm.SHA512
    }

    companion object {

        fun from(value: Int): EntryAlgorithm = when (value) {
            SHA1.value -> SHA1
            SHA256.value -> SHA256
            SHA512.value -> SHA512
            else -> throw IllegalArgumentException("Unknown ${EntryAlgorithm::class.simpleName}: $value")
        }

    }

}
