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
import android.os.Build
import android.os.LocaleList
import androidx.startup.Initializer
import me.proton.core.util.android.sentry.TimberLogger
import me.proton.core.util.kotlin.CoreLogger
import proton.android.authenticator.BuildConfig
import proton.android.authenticator.common.AuthenticatorLogger
import proton.android.authenticator.common.deviceInfo
import timber.log.Timber

abstract class LoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Forward Core Logs to Timber, using TimberLogger.
        CoreLogger.set(TimberLogger)

        deviceInfo(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()
}
