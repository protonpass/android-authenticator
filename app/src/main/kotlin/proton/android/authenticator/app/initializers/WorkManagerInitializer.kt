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

package proton.android.authenticator.app.initializers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

internal class WorkManagerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            WorkManagerInitializerEntryPoint::class.java
        )
            .hiltWorkerFactory()
            .let { workerFactory ->
                Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()
            }
            .also { configuration -> WorkManager.initialize(context, configuration) }
    }


    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface WorkManagerInitializerEntryPoint {

        fun hiltWorkerFactory(): HiltWorkerFactory

    }

}
