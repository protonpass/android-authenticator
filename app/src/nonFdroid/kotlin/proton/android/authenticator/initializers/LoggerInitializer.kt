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
package proton.android.authenticator.initializers

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import me.proton.core.util.android.sentry.TimberLogger
import me.proton.core.util.kotlin.CoreLogger
import proton.android.authenticator.BuildConfig
import proton.android.authenticator.common.deviceInfo
import proton.android.authenticator.commonrust.AuthenticatorLogger
import proton.android.authenticator.commonrust.registerAuthenticatorLogger
import timber.log.Timber

internal class LoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                LoggerInitializerDependencies::class.java
            )
        ) {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }

            // Forward Core Logs to Timber, using TimberLogger.
            CoreLogger.set(TimberLogger)

            registerAuthenticatorLogger(getAuthenticatorLogger())

            deviceInfo(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface LoggerInitializerDependencies {

        fun getAuthenticatorLogger(): AuthenticatorLogger

    }

}
