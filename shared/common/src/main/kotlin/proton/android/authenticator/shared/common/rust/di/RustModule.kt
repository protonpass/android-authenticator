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

package proton.android.authenticator.shared.common.rust.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.commonrust.AuthenticatorCrypto
import proton.android.authenticator.commonrust.AuthenticatorCryptoInterface
import proton.android.authenticator.commonrust.AuthenticatorImporter
import proton.android.authenticator.commonrust.AuthenticatorImporterInterface
import proton.android.authenticator.commonrust.AuthenticatorIssuerMapper
import proton.android.authenticator.commonrust.AuthenticatorIssuerMapperInterface
import proton.android.authenticator.commonrust.AuthenticatorLogger
import proton.android.authenticator.commonrust.AuthenticatorMobileClient
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.commonrust.MobileCurrentTimeProvider
import proton.android.authenticator.commonrust.MobileTotpGenerator
import proton.android.authenticator.commonrust.MobileTotpGeneratorInterface
import proton.android.authenticator.commonrust.SyncOperationChecker
import proton.android.authenticator.commonrust.SyncOperationCheckerInterface
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.shared.common.rust.RustAuthenticatorLogger
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal object RustModule {

    @[Provides Singleton]
    internal fun provideAuthenticatorCrypto(): AuthenticatorCryptoInterface = AuthenticatorCrypto()

    @[Provides Singleton]
    internal fun provideAuthenticatorLogger(): AuthenticatorLogger = RustAuthenticatorLogger()

    @[Provides Singleton]
    internal fun provideAuthenticatorImporter(): AuthenticatorImporterInterface = AuthenticatorImporter()

    @[Provides Singleton]
    internal fun provideAuthenticatorIssuerMapper(): AuthenticatorIssuerMapperInterface = AuthenticatorIssuerMapper()

    @[Provides Singleton]
    internal fun provideAuthenticatorMobileClient(): AuthenticatorMobileClientInterface = AuthenticatorMobileClient()

    @[Provides Singleton]
    internal fun provideMobileTotpGenerator(timeProvider: TimeProvider): MobileTotpGeneratorInterface =
        MobileTotpGenerator(
            periodMs = 500u,
            onlyOnCodeChange = true,
            currentTime = object : MobileCurrentTimeProvider {
                override fun now(): ULong = timeProvider.currentInstant()
                    .epochSeconds
                    .toULong()
            }
        )

    @[Provides Singleton]
    internal fun provideSyncOperationChecker(): SyncOperationCheckerInterface = SyncOperationChecker()

}
