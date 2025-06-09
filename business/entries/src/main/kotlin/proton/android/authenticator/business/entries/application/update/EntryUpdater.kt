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
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.IssuerInfo
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

internal class EntryUpdater @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val repository: EntriesRepository,
    private val timeProvider: TimeProvider
) {

    internal suspend fun update(
        id: String,
        position: Double,
        model: AuthenticatorEntryModel,
        issuerInfo: IssuerInfo?
    ) {
        authenticatorClient.serializeEntry(model)
            .let { decryptedModelContent ->
                encryptionContextProvider.withEncryptionContext {
                    encrypt(decryptedModelContent, EncryptionTag.EntryContent)
                }
            }
            .let { encryptedContent ->
                encryptedContent to repository.find(id = id).first()
            }
            .let { (encryptedContent, currentEntry) ->
                Entry(
                    id = id,
                    content = encryptedContent,
                    createdAt = currentEntry.createdAt,
                    modifiedAt = timeProvider.currentMillis(),
                    isSynced = false,
                    position = position,
                    iconUrl = issuerInfo?.iconUrl
                )
            }
            .also { updatedEntry ->
                repository.save(updatedEntry)
            }
    }

}
