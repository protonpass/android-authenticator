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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.shared.common.dispatchers.AppDispatchersImpl
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import proton.android.authenticator.shared.common.infrastructure.commands.InMemoryCommandBus
import proton.android.authenticator.shared.common.infrastructure.queries.InMemoryQueryBus
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class SharedCommonModule {

    @[Binds Singleton]
    internal abstract fun bindAppDispatchers(impl: AppDispatchersImpl): AppDispatchers

    @[Binds Singleton]
    internal abstract fun bindCommandBus(impl: InMemoryCommandBus): CommandBus

    @[Binds Singleton]
    internal abstract fun bindQueryBus(impl: InMemoryQueryBus): QueryBus

}
