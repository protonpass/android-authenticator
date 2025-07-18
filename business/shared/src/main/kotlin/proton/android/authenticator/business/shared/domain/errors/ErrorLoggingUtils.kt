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

package proton.android.authenticator.business.shared.domain.errors

import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.answers.AnswerReason
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger

/**
 * Utility class for logging errors and returning failure answers.
 */
data object ErrorLoggingUtils {

    /**
     * Logs an error and returns a failure answer with the specified reason.
     *
     * @param exception The exception that occurred
     * @param message The error message to log
     * @param reason The failure reason to return
     * @param tag The tag for logging (usually the class name)
     * @return An Answer.Failure with the specified reason
     */
    inline fun <reified T : AnswerReason> logAndReturnFailure(
        exception: Exception,
        message: String,
        reason: T,
        tag: String
    ): Answer<Nothing, T> {
        AuthenticatorLogger.w(tag, message)
        AuthenticatorLogger.w(tag, exception)
        return Answer.Failure(reason = reason)
    }
}
