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

package proton.android.authenticator.business.entries.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.entries.application.create.CreateEntryCommand
import proton.android.authenticator.business.entries.application.create.CreateEntryCommandHandler
import proton.android.authenticator.business.entries.application.delete.DeleteEntryCommand
import proton.android.authenticator.business.entries.application.delete.DeleteEntryCommandHandler
import proton.android.authenticator.business.entries.application.exportall.ExportEntriesCommand
import proton.android.authenticator.business.entries.application.exportall.ExportEntriesCommandHandler
import proton.android.authenticator.business.entries.application.find.FindEntryQuery
import proton.android.authenticator.business.entries.application.find.FindEntryQueryHandler
import proton.android.authenticator.business.entries.application.findall.FindAllEntriesQuery
import proton.android.authenticator.business.entries.application.findall.FindAllEntriesQueryHandler
import proton.android.authenticator.business.entries.application.importall.ImportEntriesCommand
import proton.android.authenticator.business.entries.application.importall.ImportEntriesCommandHandler
import proton.android.authenticator.business.entries.application.update.UpdateEntryCommand
import proton.android.authenticator.business.entries.application.update.UpdateEntryCommandHandler
import proton.android.authenticator.business.entries.domain.EntriesRepository
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.infrastructure.EntriesRepositoryImpl
import proton.android.authenticator.business.entries.infrastructure.persistence.room.RoomEntriesPersistenceDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessEntriesModule {

    @[Binds Singleton IntoMap CommandHandlerKey(CreateEntryCommand.FromSteam::class)]
    internal abstract fun bindCreateEntryFromSteamCommandHandler(
        impl: CreateEntryCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(CreateEntryCommand.FromTotp::class)]
    internal abstract fun bindCreateEntryFromTotpCommandHandler(
        impl: CreateEntryCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(CreateEntryCommand.FromUri::class)]
    internal abstract fun bindCreateEntryFromUriCommandHandler(impl: CreateEntryCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(DeleteEntryCommand::class)]
    internal abstract fun bindDeleteEntryCommandHandler(impl: DeleteEntryCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(ExportEntriesCommand::class)]
    internal abstract fun bindExportEntriesCommandHandler(impl: ExportEntriesCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap QueryHandlerKey(FindEntryQuery::class)]
    internal abstract fun bindFindEntryQueryHandler(impl: FindEntryQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap QueryHandlerKey(FindAllEntriesQuery::class)]
    internal abstract fun bindFindAllEntriesQueryHandler(impl: FindAllEntriesQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap CommandHandlerKey(ImportEntriesCommand::class)]
    internal abstract fun bindImportEntriesCommandHandler(impl: ImportEntriesCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateEntryCommand.FromSteam::class)]
    internal abstract fun bindUpdateEntryFromSteamCommandHandler(
        impl: UpdateEntryCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateEntryCommand.FromTotp::class)]
    internal abstract fun bindUpdateEntryFromTotpCommandHandler(
        impl: UpdateEntryCommandHandler
    ): CommandHandler<*, *, *>

    @[Binds Singleton]
    internal abstract fun bindEntriesRepository(impl: EntriesRepositoryImpl): EntriesRepository

    @[Binds Singleton]
    internal abstract fun bindEntriesPersistenceDataSource(
        impl: RoomEntriesPersistenceDataSource
    ): PersistenceDataSource<Entry>

}
