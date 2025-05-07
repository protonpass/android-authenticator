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

package proton.android.authenticator.shared.common.di

import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.shared.common.checkers.AndroidAppInstalledChecker
import proton.android.authenticator.shared.common.dispatchers.AppDispatchersImpl
import proton.android.authenticator.shared.common.domain.checkers.AppInstalledChecker
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import proton.android.authenticator.shared.common.infrastructure.commands.InMemoryCommandBus
import proton.android.authenticator.shared.common.infrastructure.queries.InMemoryQueryBus
import proton.android.authenticator.shared.common.providers.ClockTimeProvider
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class SharedCommonModule {

    @[Binds Singleton]
    internal abstract fun bindAppDispatchers(impl: AppDispatchersImpl): AppDispatchers

    @[Binds Singleton]
    internal abstract fun bindAppInstalledChecker(impl: AndroidAppInstalledChecker): AppInstalledChecker

    @[Binds Singleton]
    internal abstract fun bindCommandBus(impl: InMemoryCommandBus): CommandBus

    @[Binds Singleton]
    internal abstract fun bindTimeProvider(impl: ClockTimeProvider): TimeProvider

    @[Binds Singleton]
    internal abstract fun bindQueryBus(impl: InMemoryQueryBus): QueryBus

    internal companion object {

        @[Provides Singleton]
        internal fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager =
            context.getSystemService(ClipboardManager::class.java) as ClipboardManager

        @[Provides Singleton]
        internal fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
            context.contentResolver

        @[Provides Singleton]
        internal fun providePackageManager(@ApplicationContext context: Context): PackageManager =
            context.packageManager

    }

}
