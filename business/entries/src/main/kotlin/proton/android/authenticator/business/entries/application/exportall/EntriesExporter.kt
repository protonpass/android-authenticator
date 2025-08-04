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
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

internal class EntriesExporter @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val fileWriter: FileWriter,
    private val repository: EntriesRepository
) {

    suspend fun export(destinationUri: Uri, password: String?): Int = repository.findAll()
        .first()
        .let { entries ->
            encryptionContextProvider.withEncryptionContext {
                entries.map { entry ->
                    authenticatorClient.deserializeEntry(
                        serialized = decrypt(entry.content, EncryptionTag.EntryContent)
                    )
                }
            }
        }
        .let { entryModels ->
            entryModels.size to withContext(appDispatchers.default) {
                if (password == null) {
                    authenticatorClient.exportEntries(entryModels)
                } else {
                    authenticatorClient.exportEntriesWithPassword(entryModels, password)
                }
            }
        }
        .let { (modelsCount, content) ->
            fileWriter.write(destinationUri, content)

            modelsCount
        }

}
