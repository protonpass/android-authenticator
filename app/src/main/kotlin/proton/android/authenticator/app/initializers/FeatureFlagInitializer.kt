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
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import me.proton.core.featureflag.data.FeatureFlagRefreshStarter
import proton.android.authenticator.BuildConfig

internal class FeatureFlagInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                FeatureFlagInitializerDependencies::class.java
            )
        ) {
            getFeatureFlagRefreshStarter().start(immediately = BuildConfig.DEBUG)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface FeatureFlagInitializerDependencies {

        fun getFeatureFlagRefreshStarter(): FeatureFlagRefreshStarter

    }

}
