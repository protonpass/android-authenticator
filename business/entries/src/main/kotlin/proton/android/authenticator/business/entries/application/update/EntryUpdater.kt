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
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntryUpdateContents
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import javax.inject.Inject

internal class EntryUpdater @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val timeProvider: TimeProvider,
    private val entriesRepository: EntriesRepository
) {

    internal suspend fun update(model: AuthenticatorEntryModel) {
        val params = authenticatorClient.getTotpParams(model)

        authenticatorClient.updateEntry(
            entry = model,
            update = AuthenticatorEntryUpdateContents(
                name = model.name,
                secret = model.secret,
                issuer = model.issuer,
                period = model.period,
                note = model.note,
                entryType = model.entryType,
                digits = params.digits,
                algorithm = params.algorithm
            )
        )
            .let { updatedModel ->
                entriesRepository.find(id = updatedModel.id)
                    .first()
                    .copy(
                        model = updatedModel,
                        params = params,
                        modifiedAt = timeProvider.currentMillis()
                    )
            }
            .also { updatedEntry ->
                entriesRepository.save(updatedEntry)
            }
    }

}
