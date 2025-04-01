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

package proton.android.authenticator.protonapps.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.protonapps.application.findall.FindAllProtonAppsQuery
import proton.android.authenticator.protonapps.application.findall.FindAllProtonAppsQueryHandler
import proton.android.authenticator.protonapps.domain.ProtonAppsLocalDataSource
import proton.android.authenticator.protonapps.domain.ProtonAppsRepository
import proton.android.authenticator.protonapps.infrastructure.ProtonAppsRepositoryImpl
import proton.android.authenticator.protonapps.infrastructure.persistence.memory.InMemoryProtonAppsLocalDataSource
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessProtonAppsModule {

    @[Binds Singleton IntoMap QueryHandlerKey(FindAllProtonAppsQuery::class)]
    internal abstract fun bindAllProtonAppsQueryHandler(impl: FindAllProtonAppsQueryHandler): QueryHandler<*, *>

    @[Binds Singleton]
    internal abstract fun bindProtonAppsRepository(impl: ProtonAppsRepositoryImpl): ProtonAppsRepository

    @[Binds Singleton]
    internal abstract fun bindProtonAppsLocalDataSource(
        impl: InMemoryProtonAppsLocalDataSource
    ): ProtonAppsLocalDataSource

}
