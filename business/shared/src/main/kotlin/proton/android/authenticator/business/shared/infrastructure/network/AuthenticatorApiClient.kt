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

package proton.android.authenticator.business.shared.infrastructure.network

import me.proton.core.network.domain.ApiClient
import proton.android.authenticator.business.shared.domain.network.NetworkConfig
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import javax.inject.Inject

internal class AuthenticatorApiClient @Inject constructor(config: NetworkConfig) : ApiClient {

    override val appVersionHeader: String = config.appVersionHeader

    override val enableDebugLogging: Boolean = config.enableDebugLogging

    override val shouldUseDoh: Boolean = config.shouldUseDoh

    override val userAgent: String = config.userAgent

    override fun forceUpdate(errorMessage: String) {
        AuthenticatorLogger.i(tag = TAG, message = errorMessage)
    }

    private companion object {

        private const val TAG = "AuthenticatorApiClient"

    }

}
