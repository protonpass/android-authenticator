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

package proton.android.authenticator.business.keys.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.keys.application.create.CreateKeyCommand
import proton.android.authenticator.business.keys.application.create.CreateKeyCommandHandler
import proton.android.authenticator.business.keys.application.findall.FindAllKeysQuery
import proton.android.authenticator.business.keys.application.findall.FindAllKeysQueryHandler
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.keys.domain.KeysApi
import proton.android.authenticator.business.keys.domain.KeysRepository
import proton.android.authenticator.business.keys.infrastructure.KeysApiImpl
import proton.android.authenticator.business.keys.infrastructure.KeysRepositoryImpl
import proton.android.authenticator.business.keys.infrastructure.persistence.room.RoomKeysPersistenceDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessKeysModule {

    @[Binds Singleton IntoMap CommandHandlerKey(CreateKeyCommand::class)]
    internal abstract fun bindCreateKeyCommandHandler(impl: CreateKeyCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap QueryHandlerKey(FindAllKeysQuery::class)]
    internal abstract fun bindFindAllKeysQueryHandler(impl: FindAllKeysQueryHandler): QueryHandler<*, *>

    @[Binds Singleton]
    internal abstract fun bindKeysApi(impl: KeysApiImpl): KeysApi

    @[Binds Singleton]
    internal abstract fun bindKeysRepository(impl: KeysRepositoryImpl): KeysRepository

    @[Binds Singleton]
    internal abstract fun bindKeysPersistenceDataSource(impl: RoomKeysPersistenceDataSource): PersistenceDataSource<Key>

}
