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

import android.net.Uri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import javax.inject.Inject

internal class EntriesExporter @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val repository: EntriesRepository,
    private val fileWriter: FileWriter
) {

    suspend fun export(destinationUri: Uri): Int = getEntryModels()
        .let { models ->
            models.size to createFileContent(models)
        }
        .let { (modelsCount, content) ->
            fileWriter.write(destinationUri.toString(), content)

            modelsCount
        }

    private suspend fun getEntryModels() = repository.findAll()
        .first()
        .map(Entry::toModel)

    private suspend fun createFileContent(models: List<AuthenticatorEntryModel>) = withContext(appDispatchers.default) {
        authenticatorClient.exportEntries(models)
    }

}

private fun Entry.toModel() = AuthenticatorEntryModel(
    id = id,
    name = name,
    issuer = issuer,
    secret = secret,
    uri = uri,
    period = period.toUShort(),
    note = note,
    entryType = type.asAuthenticatorEntryType()
)
