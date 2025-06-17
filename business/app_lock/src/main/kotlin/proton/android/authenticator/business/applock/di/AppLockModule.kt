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

package proton.android.authenticator.business.applock.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.applock.application.find.FindAppLockStateQuery
import proton.android.authenticator.business.applock.application.find.FindAppLockStateQueryHandler
import proton.android.authenticator.business.applock.application.update.UpdateAppLockStateCommand
import proton.android.authenticator.business.applock.application.update.UpdateAppLockStateCommandHandler
import proton.android.authenticator.business.applock.domain.AppLockRepository
import proton.android.authenticator.business.applock.infrastructure.AppLockRepositoryImpl
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class AppLockModule {

    @[Binds Singleton IntoMap QueryHandlerKey(FindAppLockStateQuery::class)]
    internal abstract fun bindFindAppLockStateQueryHandler(impl: FindAppLockStateQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateAppLockStateCommand::class)]
    internal abstract fun bindUpdateAppLockStateCommandHandler(
        impl: UpdateAppLockStateCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton]
    internal abstract fun bindAppLockRepository(impl: AppLockRepositoryImpl): AppLockRepository
}
