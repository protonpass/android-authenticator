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

import android.os.Build
import me.proton.core.configuration.EnvironmentConfiguration
import me.proton.core.humanverification.presentation.utils.HumanVerificationVersion
import me.proton.core.network.data.di.Constants
import me.proton.core.util.kotlin.takeIfNotBlank
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import proton.android.authenticator.business.shared.domain.app.AppConfig
import proton.android.authenticator.business.shared.domain.network.NetworkConfig
import javax.inject.Inject

internal class AuthenticatorNetworkConfig @Inject constructor(
    appConfig: AppConfig,
    envConfig: EnvironmentConfiguration
) : NetworkConfig {

    override val alternativeApiPins: List<String> = if (envConfig.useDefaultPins) {
        Constants.ALTERNATIVE_API_SPKI_PINS
    } else {
        emptyList()
    }

    override val appVersionHeader: String = "android-authenticator@${appConfig.versionName}"

    override val certificatePins: Array<String> = if (envConfig.useDefaultPins) {
        Constants.DEFAULT_SPKI_PINS
    } else {
        emptyArray()
    }

    override val dohProvidersUrls: Array<String> = Constants.DOH_PROVIDERS_URLS

    override val enableDebugLogging: Boolean = appConfig.isDebug

    override val headers: Array<Pair<String, String>> = envConfig.proxyToken
        .takeIfNotBlank()
        ?.let { proxyToken -> arrayOf("X-atlas-secret" to proxyToken) }
        ?: emptyArray()

    override val hostHttpUrl: HttpUrl = "https://${envConfig.apiHost}".toHttpUrl()

    override val humanVerificationHost: String = "https://${envConfig.hv3Host}/"

    override val humanVerificationVersion: HumanVerificationVersion = HumanVerificationVersion.HV3

    override val shouldUseDoh: Boolean = false

    override val userAgent: String = buildString {
        append("ProtonAuthenticator/${appConfig.versionName}")
        append(" ")
        append("(")
        append("Android ${Build.VERSION.RELEASE};")
        append(" ")
        append("${Build.BRAND} ${Build.MODEL}")
        append(")")
    }

}
