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
import proton.android.authenticator.business.entries.application.find.FindEntryQuery
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import proton.android.authenticator.shared.crypto.domain.contexts.EncryptionContextProvider
import proton.android.authenticator.shared.crypto.domain.tags.EncryptionTag
import javax.inject.Inject

class GetEntryModelUseCase @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val encryptionContextProvider: EncryptionContextProvider,
    private val queryBus: QueryBus
) {

    suspend operator fun invoke(entryId: String): EntryModel = FindEntryQuery(id = entryId)
        .let { query -> queryBus.ask<Entry>(query) }
        .first()
        .let { entry ->
            encryptionContextProvider.withEncryptionContext {
                authenticatorClient.deserializeEntry(
                    serialized = decrypt(entry.content, EncryptionTag.EntryContent)
                ).let { authenticatorEntryModel ->
                    authenticatorEntryModel to authenticatorClient.getTotpParams(
                        entry = authenticatorEntryModel
                    )
                }.let { (authenticatorEntryModel, authenticatorTotpParams) ->
                    EntryModel(
                        id = entry.id,
                        position = entry.position,
                        iconUrl = entry.iconUrl,
                        createdAt = entry.createdAt,
                        modifiedAt = entry.modifiedAt,
                        name = authenticatorEntryModel.name,
                        issuer = authenticatorEntryModel.issuer,
                        note = authenticatorEntryModel.note,
                        secret = authenticatorEntryModel.secret,
                        uri = authenticatorEntryModel.uri,
                        period = authenticatorEntryModel.period.toInt(),
                        type = EntryType.from(authenticatorEntryModel.entryType.ordinal),
                        algorithm = EntryAlgorithm.from(authenticatorTotpParams.algorithm.ordinal),
                        digits = authenticatorTotpParams.digits.toInt()
                    )
                }
            }
        }

}
