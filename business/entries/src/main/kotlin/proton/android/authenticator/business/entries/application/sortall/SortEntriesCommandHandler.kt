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

package proton.android.authenticator.business.entries.application.sortall

import proton.android.authenticator.business.shared.domain.errors.ErrorLoggingUtils
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class SortEntriesCommandHandler @Inject constructor(
    private val sorter: EntriesSorter
) : CommandHandler<SortEntriesCommand, Unit, SortEntriesReason> {

    override suspend fun handle(command: SortEntriesCommand): Answer<Unit, SortEntriesReason> = try {
        sorter.sort(command.sortingMap)
            .also { AuthenticatorLogger.i(TAG, "Entries successfully sorted") }
            .let(Answer<Unit, SortEntriesReason>::Success)
    } catch (exception: IllegalStateException) {
        ErrorLoggingUtils.logAndReturnFailure(
            tag = TAG,
            exception = exception,
            message = "Could sort entries due to database error",
            reason = SortEntriesReason.CannotSort
        )
    }

    private companion object {

        private const val TAG = "SortEntriesCommandHandler"

    }

}
