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

package proton.android.authenticator.business.entrycodes.application.search

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.commonrust.AuthenticatorCodeResponse
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.MobileTotpGenerator
import proton.android.authenticator.commonrust.MobileTotpGeneratorCallback
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import javax.inject.Inject

internal class EntryCodesSearcher @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val totpGenerator: MobileTotpGenerator,
    private val appDispatchers: AppDispatchers
) {

    internal fun search(uris: List<String>): Flow<List<EntryCode>> = callbackFlow {
        uris.map { uri ->
            authenticatorClient.entryFromUri(uri)
        }.let { entryModels ->
            totpGenerator.start(
                entries = entryModels,
                callback = object : MobileTotpGeneratorCallback {
                    override fun onCodes(codes: List<AuthenticatorCodeResponse>) {
                        codes.map { entryCodeResponse ->
                            EntryCode.fromPrimitives(
                                currentCode = entryCodeResponse.currentCode,
                                nextCode = entryCodeResponse.nextCode,
                                remainingSeconds = 44
                            )
                        }.also(::trySend)
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
