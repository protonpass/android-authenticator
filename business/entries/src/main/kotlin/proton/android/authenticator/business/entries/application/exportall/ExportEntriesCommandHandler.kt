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

package proton.android.authenticator.business.entries.application.exportall

import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

internal class ExportEntriesCommandHandler @Inject constructor(
    private val entriesExporter: EntriesExporter
) : CommandHandler<ExportEntriesCommand, Int, ExportEntriesReason> {

    override suspend fun handle(command: ExportEntriesCommand): Answer<Int, ExportEntriesReason> = try {
        entriesExporter.export(destinationUri = command.destinationUri)
            .let(Answer<Int, ExportEntriesReason>::Success)
    } catch (_: AuthenticatorException) {
        Answer.Failure(ExportEntriesReason.InvalidEntries)
    } catch (_: FileNotFoundException) {
        Answer.Failure(ExportEntriesReason.InvalidEntries)
    } catch (_: IOException) {
        Answer.Failure(ExportEntriesReason.InvalidEntries)
    }

}
