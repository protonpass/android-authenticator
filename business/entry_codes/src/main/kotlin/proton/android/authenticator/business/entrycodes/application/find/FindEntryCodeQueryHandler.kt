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

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import proton.android.authenticator.business.entrycodes.application.shared.responses.EntryCodeQueryResponse
import proton.android.authenticator.business.entrycodes.application.shared.responses.toQueryResponse
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Inject
import kotlin.math.floor

internal class FindEntryCodeQueryHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val clock: Clock,
    private val appDispatchers: AppDispatchers
) : QueryHandler<FindEntryCodeQuery, EntryCodeQueryResponse> {

    override fun handle(query: FindEntryCodeQuery): Flow<EntryCodeQueryResponse> = flow {
        while (currentCoroutineContext().isActive) {
            authenticatorClient.entryFromUri(uri = query.uri)
                .let { entryModel ->
                    val epochSeconds = clock.now()
                        .toJavaInstant()
                        .epochSecond

                    authenticatorClient.generateCodes(
                        entries = listOf(entryModel),
                        time = epochSeconds.toULong()
                    )
                        .first()
                        .let { entryCodeResponse ->
                            EntryCode.fromPrimitives(
                                currentCode = entryCodeResponse.currentCode,
                                nextCode = entryCodeResponse.nextCode,
                                remainingSeconds = entryModel.period
                                    .toInt() - (floor(epochSeconds.toDouble()) % entryModel.period.toInt()).toInt()
                            )
                        }
                }
                .let(EntryCode::toQueryResponse)
                .also { entryCodeQueryResponse ->
                    emit(entryCodeQueryResponse)
                }

            delay(ENTRY_CODE_REFRESH_RATE_MILLIS)
        }
    }.flowOn(appDispatchers.default)

    private companion object {

        private const val ENTRY_CODE_REFRESH_RATE_MILLIS = 1_000L

    }

}
