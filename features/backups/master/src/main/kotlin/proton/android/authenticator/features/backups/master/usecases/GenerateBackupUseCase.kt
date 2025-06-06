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

package proton.android.authenticator.features.backups.master.usecases

import proton.android.authenticator.business.backups.application.generate.GenerateBackupCommand
import proton.android.authenticator.business.backups.application.generate.GenerateBackupReason
import proton.android.authenticator.business.backups.domain.BackupEntry
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

internal class GenerateBackupUseCase @Inject constructor(private val commandBus: CommandBus) {

    internal suspend operator fun invoke(entries: List<Entry>): Answer<Unit, GenerateBackupReason> = entries
        .map { entry ->
            BackupEntry(
                id = entry.id,
                name = entry.name,
                uri = entry.uri,
                period = entry.period.toUShort(),
                issuer = entry.issuer,
                secret = entry.secret,
                note = entry.note,
                entryTypeOrdinal = entry.type.ordinal
            )
        }
        .let(::GenerateBackupCommand)
        .let { command -> commandBus.dispatch(command) }

}
