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

package proton.android.authenticator.business.shared.infrastructure.files.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileDeleter
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileReader
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileReader
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileWriter
import proton.android.authenticator.business.shared.infrastructure.files.InternalFileDeleter
import proton.android.authenticator.business.shared.infrastructure.files.InternalFileWriter
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSharedFilesModule {

    @[Binds Singleton]
    internal abstract fun bindFileReader(impl: ContentResolverFileReader): FileReader

    @[Binds Singleton FileWriterContentResolver]
    internal abstract fun bindContentResolverFileWriter(impl: ContentResolverFileWriter): FileWriter

    @[Binds Singleton FileDeleterInternal]
    internal abstract fun bindInternalFileDeleter(impl: InternalFileDeleter): FileDeleter

    @[Binds Singleton FileWriterInternal]
    internal abstract fun bindInternalFileWriter(impl: InternalFileWriter): FileWriter

}
