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

package proton.android.authenticator.business.backups.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.backups.application.find.FindBackupQuery
import proton.android.authenticator.business.backups.application.find.FindBackupQueryHandler
import proton.android.authenticator.business.backups.application.update.UpdateBackupCommand
import proton.android.authenticator.business.backups.application.update.UpdateBackupCommandHandler
import proton.android.authenticator.business.backups.domain.Backup
import proton.android.authenticator.business.backups.domain.BackupRepository
import proton.android.authenticator.business.backups.infrastructure.BackupRepositoryImpl
import proton.android.authenticator.business.backups.infrastructure.preferences.datastore.DataStoreBackupPreferencesDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessBackupModule {

    @[Binds Singleton IntoMap QueryHandlerKey(FindBackupQuery::class)]
    internal abstract fun bindFindBackupQueryHandler(impl: FindBackupQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateBackupCommand::class)]
    internal abstract fun bindUpdateBackupCommandHandler(impl: UpdateBackupCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton]
    internal abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

    @[Binds Singleton]
    internal abstract fun bindBackupsPreferencesDataSource(
        impl: DataStoreBackupPreferencesDataSource
    ): PreferencesDataSource<Backup>

}
