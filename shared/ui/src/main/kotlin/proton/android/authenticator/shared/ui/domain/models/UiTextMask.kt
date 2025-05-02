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

package proton.android.authenticator.shared.ui.domain.models

sealed class UiTextMask {

    internal abstract fun apply(original: String): String

    data object Hidden : UiTextMask() {

        override fun apply(original: String): String = '•'.toString().repeat(original.length)

    }

    data object Totp : UiTextMask() {

        override fun apply(original: String): String {
            if (original.length == TOTP_CODE_LENGTH_DEFAULT) {
                return original
                    .chunked(size = TOTP_CODE_LENGTH_DEFAULT.div(2))
                    .joinToString(separator = " ")
            }

            return original
        }

    }

    private companion object {

        private const val TOTP_CODE_LENGTH_DEFAULT = 6

    }

}
