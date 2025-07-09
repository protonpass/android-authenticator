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

package proton.android.authenticator.business.entries.application.unsyncall

import proton.android.authenticator.business.shared.domain.errors.ErrorLoggingUtils
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class UnsyncEntriesCommandHandler @Inject constructor(
    private val unsyncer: EntriesUnsyncer
) : CommandHandler<UnsyncEntriesCommand, Unit, UnsyncEntriesReason> {

    override suspend fun handle(command: UnsyncEntriesCommand): Answer<Unit, UnsyncEntriesReason> = try {
        unsyncer.unsyncAll()
            .also { AuthenticatorLogger.i(TAG, "Successfully unsyced entries") }
            .let(Answer<Unit, UnsyncEntriesReason>::Success)
    } catch (exception: IllegalStateException) {
        ErrorLoggingUtils.logAndReturnFailure(
            tag = TAG,
            exception = exception,
            reason = UnsyncEntriesReason.CannotUnsyncEntries,
            message = "Could not unsync entries due to database operation error"
        )
    }

    private companion object {

        private const val TAG = "UnsyncEntriesCommandHandler"

    }

}
