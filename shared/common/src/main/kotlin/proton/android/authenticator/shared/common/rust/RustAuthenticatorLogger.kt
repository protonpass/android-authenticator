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

package proton.android.authenticator.shared.common.rust

import proton.android.authenticator.commonrust.AuthenticatorLogLevel
import proton.android.authenticator.commonrust.AuthenticatorLogger
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger as RustLogger

internal class RustAuthenticatorLogger : AuthenticatorLogger {

    override fun log(level: AuthenticatorLogLevel, message: String) {
        when (level) {
            AuthenticatorLogLevel.DEBUG -> RustLogger.d(TAG, message)
            AuthenticatorLogLevel.ERROR -> RustLogger.w(TAG, message)
            AuthenticatorLogLevel.INFO -> RustLogger.i(TAG, message)
            AuthenticatorLogLevel.TRACE -> RustLogger.v(TAG, message)
            AuthenticatorLogLevel.WARN -> RustLogger.w(TAG, message)
        }
    }

    private companion object {

        private const val TAG = "RustAuthenticatorLogger"

    }

}
