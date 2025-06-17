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

package proton.android.authenticator.business.shared.infrastructure.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.proton.core.humanverification.presentation.HumanVerificationApiHost
import me.proton.core.humanverification.presentation.utils.HumanVerificationVersion
import me.proton.core.network.data.client.ExtraHeaderProviderImpl
import me.proton.core.network.data.di.AlternativeApiPins
import me.proton.core.network.data.di.BaseProtonApiUrl
import me.proton.core.network.data.di.CertificatePins
import me.proton.core.network.data.di.DohProviderUrls
import me.proton.core.network.domain.ApiClient
import me.proton.core.network.domain.client.ExtraHeaderProvider
import me.proton.core.network.domain.serverconnection.DohAlternativesListener
import okhttp3.HttpUrl
import proton.android.authenticator.business.shared.domain.network.NetworkConfig
import proton.android.authenticator.business.shared.infrastructure.network.AuthenticatorApiClient
import proton.android.authenticator.business.shared.infrastructure.network.AuthenticatorNetworkConfig
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSharedNetworkModule {

    @[Binds Singleton]
    internal abstract fun bindApiClient(impl: AuthenticatorApiClient): ApiClient

    @[Binds Singleton]
    internal abstract fun bindNetworkConfig(impl: AuthenticatorNetworkConfig): NetworkConfig

    internal companion object {

        @[Provides BaseProtonApiUrl]
        internal fun provideProtonApiUrl(config: NetworkConfig): HttpUrl = config.hostHttpUrl

        @[Provides Singleton]
        internal fun provideExtraHeaderProvider(config: NetworkConfig): ExtraHeaderProvider =
            ExtraHeaderProviderImpl().apply { addHeaders(*config.headers) }

        @[Provides Singleton DohProviderUrls]
        internal fun provideDohProviderUrls(config: NetworkConfig): Array<String> = config.dohProvidersUrls

        @[Provides Singleton CertificatePins]
        internal fun provideCertificatePins(config: NetworkConfig): Array<String> = config.certificatePins

        @[Provides Singleton AlternativeApiPins]
        internal fun provideAlternativeApiPins(config: NetworkConfig): List<String> = config.alternativeApiPins

        @[Provides Singleton]
        internal fun provideDohAlternativesListener(): DohAlternativesListener? = null

        @[Provides Singleton]
        internal fun provideHumanVerificationVersion(config: NetworkConfig): HumanVerificationVersion =
            config.humanVerificationVersion

        @[Provides HumanVerificationApiHost]
        internal fun provideHumanVerificationApiHost(config: NetworkConfig): String = config.humanVerificationHost

    }

}
