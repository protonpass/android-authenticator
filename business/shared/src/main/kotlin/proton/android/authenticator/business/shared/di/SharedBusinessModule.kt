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

package proton.android.authenticator.business.shared.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileReader
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileReader
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileWriter
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.settings.SettingsProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.room.AuthenticatorDatabase
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.proto.preferences.settings.SettingsPreferences
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class SharedBusinessModule {

    @[Binds Singleton]
    internal abstract fun bindFileReader(impl: ContentResolverFileReader): FileReader

    @[Binds Singleton]
    internal abstract fun bindFileWriter(impl: ContentResolverFileWriter): FileWriter

    internal companion object {

        @[Provides Singleton]
        internal fun provideUsersDao(database: AuthenticatorDatabase): EntriesDao = database.entriesDao()

        @[Provides Singleton]
        internal fun provideAuthenticatorDatabase(@ApplicationContext context: Context): AuthenticatorDatabase =
            Room.databaseBuilder(
                context = context,
                klass = AuthenticatorDatabase::class.java,
                name = AuthenticatorDatabase.NAME
            )
                .fallbackToDestructiveMigration(dropAllTables = false)
                .build()

        @[Provides Singleton]
        internal fun provideSettingsPreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<SettingsPreferences> = DataStoreFactory.create(
            serializer = SettingsProtoPreferencesSerializer,
            produceFile = { context.dataStoreFile("settings_preferences.pb") }
        )

    }

}
