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
import io.sentry.android.core.SentryAndroid
import proton.android.authenticator.BuildConfig
import proton.android.authenticator.initializers.BuildFlavor.Companion.toValue

class SentryInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val isSentryEnabled = BuildConfig.SENTRY_DSN?.isNotBlank() == true && !BuildConfig.DEBUG
        if (isSentryEnabled) {
            SentryAndroid.init(context) { options ->
                options.isDebug = BuildConfig.DEBUG
                options.dsn = BuildConfig.SENTRY_DSN
                options.release = BuildConfig.VERSION_NAME
                options.environment = BuildFlavor.from(BuildConfig.FLAVOR).toValue()
                options.isAttachAnrThreadDump = true
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

