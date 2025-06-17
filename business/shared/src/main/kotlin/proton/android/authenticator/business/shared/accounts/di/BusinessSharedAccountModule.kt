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

package proton.android.authenticator.business.shared.accounts.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import me.proton.core.account.domain.entity.AccountType
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.auth.domain.usecase.PostLoginAccountSetup
import me.proton.core.auth.presentation.DefaultHelpOptionHandler
import me.proton.core.auth.presentation.DefaultUserCheck
import me.proton.core.auth.presentation.HelpOptionHandler
import me.proton.core.domain.entity.AppStore
import me.proton.core.domain.entity.Product
import me.proton.core.eventmanager.domain.EventListener
import me.proton.core.notification.data.NotificationEventListener
import me.proton.core.push.data.PushEventListener
import me.proton.core.telemetry.data.repository.TelemetryLocalDataSourceImpl
import me.proton.core.telemetry.data.repository.TelemetryRemoteDataSourceImpl
import me.proton.core.telemetry.data.repository.TelemetryRepositoryImpl
import me.proton.core.telemetry.data.usecase.IsTelemetryEnabledImpl
import me.proton.core.telemetry.data.worker.TelemetryWorkerManagerImpl
import me.proton.core.telemetry.domain.TelemetryWorkerManager
import me.proton.core.telemetry.domain.repository.TelemetryLocalDataSource
import me.proton.core.telemetry.domain.repository.TelemetryRemoteDataSource
import me.proton.core.telemetry.domain.repository.TelemetryRepository
import me.proton.core.telemetry.domain.usecase.IsTelemetryEnabled
import me.proton.core.user.data.UserAddressEventListener
import me.proton.core.user.data.UserEventListener
import me.proton.core.user.domain.UserManager
import me.proton.core.usersettings.data.UserSettingsEventListener
import proton.android.authenticator.business.shared.app.AuthenticatorAppConfig
import proton.android.authenticator.business.shared.domain.app.AppConfig
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSharedAccountModule {

    @[Binds Singleton]
    internal abstract fun bindAppConfig(impl: AuthenticatorAppConfig): AppConfig

    @[Binds Singleton]
    internal abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository

    @[Binds Singleton]
    internal abstract fun bindTelemetryLocalDataSource(impl: TelemetryLocalDataSourceImpl): TelemetryLocalDataSource

    @[Binds Singleton]
    internal abstract fun bindTelemetryRemoteDataSource(impl: TelemetryRemoteDataSourceImpl): TelemetryRemoteDataSource

    @[Binds Singleton]
    internal abstract fun bindIsTelemetryEnabled(impl: IsTelemetryEnabledImpl): IsTelemetryEnabled

    @[Binds Singleton]
    internal abstract fun bindTelemetryWorkerManager(impl: TelemetryWorkerManagerImpl): TelemetryWorkerManager

    internal companion object {

        @[Provides Singleton ElementsIntoSet JvmSuppressWildcards]
        internal fun provideEventListenerSet(
            notificationEventListener: NotificationEventListener,
            pushEventListener: PushEventListener,
            userAddressEventListener: UserAddressEventListener,
            userEventListener: UserEventListener,
            userSettingsEventListener: UserSettingsEventListener
        ): Set<EventListener<*, *>> = setOf(
            notificationEventListener,
            pushEventListener,
            userAddressEventListener,
            userEventListener,
            userSettingsEventListener
        )

        @[Provides Singleton]
        internal fun provideUserCheck(
            @ApplicationContext context: Context,
            accountManager: AccountManager,
            userManager: UserManager
        ): PostLoginAccountSetup.UserCheck = DefaultUserCheck(context, accountManager, userManager)

        @[Provides Singleton]
        internal fun provideHelpOptionHandler(): HelpOptionHandler = DefaultHelpOptionHandler()

        @[Provides Singleton]
        internal fun provideAccountType(config: AppConfig): AccountType = config.accountType

        @[Provides Singleton]
        internal fun provideProduct(config: AppConfig): Product = config.product

        @[Provides Singleton]
        internal fun provideAppStore(config: AppConfig): AppStore = config.appStore

    }

}
