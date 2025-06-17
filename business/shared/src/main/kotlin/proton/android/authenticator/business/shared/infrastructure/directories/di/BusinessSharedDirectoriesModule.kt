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

package proton.android.authenticator.business.shared.infrastructure.directories.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryCreator
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryReader
import proton.android.authenticator.business.shared.infrastructure.directories.InternalDirectoryCreator
import proton.android.authenticator.business.shared.infrastructure.directories.InternalDirectoryReader
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSharedDirectoriesModule {

    @[Binds Singleton]
    internal abstract fun bindDirectoryCreator(impl: InternalDirectoryCreator): DirectoryCreator

    @[Binds Singleton]
    internal abstract fun bindDirectoryReader(impl: InternalDirectoryReader): DirectoryReader

    internal companion object {

        @[Provides Singleton DirectoryPathInternal]
        internal fun provideDirectoryPath(@ApplicationContext context: Context): String = context
            .filesDir
            .absolutePath

    }

}
