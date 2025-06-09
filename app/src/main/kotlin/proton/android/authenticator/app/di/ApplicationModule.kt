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

package proton.android.authenticator.app.di

import android.content.Context
import androidx.work.WorkManager
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal object ApplicationModule {

    private const val IMAGE_LOADER_DISK_CACHE_DIR = "image_cache"
    private const val IMAGE_LOADER_DISK_CACHE_MAX_SIZE = 0.02
    private const val IMAGE_LOADER_MEMORY_CACHE_MAX_SIZE = 0.25

    @[Provides Singleton ApplicationCoroutineScope]
    internal fun provideCoroutineScope(): CoroutineScope = MainScope()

    @[Provides Singleton]
    internal fun provideImageLoader(@ApplicationContext context: Context): ImageLoader = ImageLoader.Builder(context)
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(IMAGE_LOADER_DISK_CACHE_DIR))
                .maxSizePercent(IMAGE_LOADER_DISK_CACHE_MAX_SIZE)
                .build()
        }
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(IMAGE_LOADER_MEMORY_CACHE_MAX_SIZE)
                .build()
        }
        .build()

    @[Provides Singleton]
    internal fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

}
