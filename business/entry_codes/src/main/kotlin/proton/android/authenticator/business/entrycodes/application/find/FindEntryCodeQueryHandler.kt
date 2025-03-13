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

package proton.android.authenticator.business.entrycodes.application.find

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import proton.android.authenticator.business.entrycodes.application.shared.responses.EntryCodeQueryResponse
import proton.android.authenticator.business.entrycodes.application.shared.responses.toQueryResponse
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.commonrust.AuthenticatorCodeResponse
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.MobileTotpGenerator
import proton.android.authenticator.commonrust.MobileTotpGeneratorCallback
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Inject

internal class FindEntryCodeQueryHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val totpGenerator: MobileTotpGenerator,
    private val appDispatchers: AppDispatchers
) : QueryHandler<FindEntryCodeQuery, EntryCodeQueryResponse> {

    override fun handle(query: FindEntryCodeQuery): Flow<EntryCodeQueryResponse> = callbackFlow {
        authenticatorClient.entryFromUri(uri = query.uri)
            .let { entryModel ->
                totpGenerator.start(
                    entries = listOf(entryModel),
                    callback = object : MobileTotpGeneratorCallback {
                        override fun onCodes(codes: List<AuthenticatorCodeResponse>) {
                            codes.first()
                                .let { entryCodeResponse ->
                                    EntryCode.fromPrimitives(
                                        currentCode = entryCodeResponse.currentCode,
                                        nextCode = entryCodeResponse.nextCode,
                                        remainingSeconds = 0
                                    )
                                }
                                .let(EntryCode::toQueryResponse)
                                .also { entryCodeQueryResponse ->
                                    trySend(entryCodeQueryResponse)
                                }
                        }
                    }
                ).also { handle ->
                    awaitClose {
                        handle.cancel()
                    }
                }
            }
    }.flowOn(appDispatchers.default)

}
