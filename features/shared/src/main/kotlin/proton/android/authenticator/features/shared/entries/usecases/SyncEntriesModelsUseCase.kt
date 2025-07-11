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

package proton.android.authenticator.features.shared.entries.usecases

import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.entries.application.syncall.SyncEntriesCommand
import proton.android.authenticator.business.entries.application.syncall.SyncEntriesReason
import proton.android.authenticator.business.entries.application.syncall.SyncEntry
import proton.android.authenticator.business.entries.application.syncall.SyncKey
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.features.shared.keys.usecases.GetKeyUseCase
import proton.android.authenticator.features.shared.users.usecases.ObserveUserUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

class SyncEntriesModelsUseCase @Inject constructor(
    private val commandBus: CommandBus,
    private val getKeyUseCase: GetKeyUseCase,
    private val observeUserUseCase: ObserveUserUseCase
) {

    suspend operator fun invoke(entryModels: List<EntryModel>): Answer<Unit, SyncEntriesReason> {
        val user = observeUserUseCase().first() ?: run {
            return Answer.Failure(reason = SyncEntriesReason.UserNotFound)
        }

        val key = getKeyUseCase() ?: run {
            return Answer.Failure(reason = SyncEntriesReason.KeyNotFound)
        }

        return SyncEntriesCommand(
            userId = user.id,
            key = SyncKey(
                id = key.id,
                encryptedKey = key.encryptedKey
            ),
            entries = entryModels.map { entryModel ->
                SyncEntry(
                    id = entryModel.id,
                    name = entryModel.name,
                    issuer = entryModel.issuer,
                    secret = entryModel.secret,
                    uri = entryModel.uri,
                    period = entryModel.period,
                    note = entryModel.note,
                    type = entryModel.type,
                    position = entryModel.position,
                    modifyTime = entryModel.modifiedAt,
                    isDeleted = entryModel.isDeleted,
                    isSynced = entryModel.isSynced
                )
            }
        ).let { command -> commandBus.dispatch(command = command) }
    }

}
