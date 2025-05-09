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

package proton.android.authenticator.business.settings.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.settings.application.find.FindSettingsQuery
import proton.android.authenticator.business.settings.application.find.FindSettingsQueryHandler
import proton.android.authenticator.business.settings.application.update.UpdateSettingsCommand
import proton.android.authenticator.business.settings.application.update.UpdateSettingsCommandHandler
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsRepository
import proton.android.authenticator.business.settings.infrastructure.SettingsRepositoryImpl
import proton.android.authenticator.business.settings.infrastructure.preferences.datastore.DataStoreSettingPreferencesDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSettingsModule {

    @[Binds Singleton IntoMap QueryHandlerKey(FindSettingsQuery::class)]
    internal abstract fun bindFindSettingsQueryHandler(impl: FindSettingsQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateSettingsCommand::class)]
    internal abstract fun bindUpdateSettingsCommandHandler(impl: UpdateSettingsCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton]
    internal abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @[Binds Singleton]
    internal abstract fun bindSettingsPreferencesDataSource(
        impl: DataStoreSettingPreferencesDataSource
    ): PreferencesDataSource<Settings>

}
