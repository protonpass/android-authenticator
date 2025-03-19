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

package proton.android.authenticator.business.entries.application.update

import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntryTotpParameters
import javax.inject.Inject

internal class EntryUpdater @Inject constructor(
    private val clock: Clock,
    private val entriesRepository: EntriesRepository
) {

    internal suspend fun update(model: AuthenticatorEntryModel, params: AuthenticatorEntryTotpParameters) {
        entriesRepository.find(id = model.id)
            .first()
            .copy(
                model = model,
                params = params,
                modifiedAt = clock.now().toEpochMilliseconds()
            )
            .also { updatedEntry ->
                entriesRepository.save(updatedEntry)
            }
    }

}
