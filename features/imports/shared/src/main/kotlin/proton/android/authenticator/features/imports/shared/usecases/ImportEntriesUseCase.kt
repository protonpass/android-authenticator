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

package proton.android.authenticator.features.imports.shared.usecases

import android.net.Uri
import proton.android.authenticator.business.entries.application.importall.ImportEntriesCommand
import proton.android.authenticator.business.entries.application.importall.ImportEntriesReason
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

class ImportEntriesUseCase @Inject constructor(private val commandBus: CommandBus) {

    suspend operator fun invoke(
        uris: List<Uri>,
        importType: EntryImportType,
        password: String? = null
    ): Answer<Int, ImportEntriesReason> = ImportEntriesCommand(uris, importType, password)
        .let { command -> commandBus.dispatch(command) }

}
