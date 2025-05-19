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

package proton.android.authenticator.business.biometrics.di

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.biometrics.application.authentication.AuthenticateBiometricCommand
import proton.android.authenticator.business.biometrics.application.authentication.AuthenticateBiometricCommandHandler
import proton.android.authenticator.business.biometrics.application.find.FindBiometricQuery
import proton.android.authenticator.business.biometrics.application.find.FindBiometricQueryHandler
import proton.android.authenticator.business.biometrics.domain.BiometricRepository
import proton.android.authenticator.business.biometrics.infrastructure.BiometricManagerBiometricRepository
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessBiometricsModule {

    @[Binds Singleton IntoMap CommandHandlerKey(AuthenticateBiometricCommand::class)]
    internal abstract fun bindAuthenticateBiometricCommandHandler(
        impl: AuthenticateBiometricCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap QueryHandlerKey(FindBiometricQuery::class)]
    internal abstract fun bindFindBiometricQueryHandler(impl: FindBiometricQueryHandler): QueryHandler<*, *>

    @[Binds Singleton]
    internal abstract fun bindBiometricRepository(impl: BiometricManagerBiometricRepository): BiometricRepository

    internal companion object {

        @[Provides Singleton]
        internal fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager =
            BiometricManager.from(context)

    }

}
