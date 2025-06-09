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

package proton.android.authenticator.features.shared.usecases.backups

import proton.android.authenticator.business.backups.application.generate.GenerateBackupCommand
import proton.android.authenticator.business.backups.application.generate.GenerateBackupReason
import proton.android.authenticator.business.backups.domain.BackupEntry
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

class GenerateBackupUseCase @Inject constructor(private val commandBus: CommandBus) {

    suspend operator fun invoke(entryModels: List<EntryModel>): Answer<Unit, GenerateBackupReason> = entryModels
        .map { entryModel ->
            BackupEntry(
                id = entryModel.id,
                name = entryModel.name,
                uri = entryModel.uri,
                period = entryModel.period.toUShort(),
                issuer = entryModel.issuer,
                secret = entryModel.secret,
                note = entryModel.note,
                entryTypeOrdinal = entryModel.type.ordinal
            )
        }
        .let(::GenerateBackupCommand)
        .let { command -> commandBus.dispatch(command) }

}
