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
import me.proton.core.auth.presentation.MissingScopeInitializer
import me.proton.core.crypto.validator.presentation.init.CryptoValidatorInitializer
import me.proton.core.humanverification.presentation.HumanVerificationInitializer
import me.proton.core.network.presentation.init.UnAuthSessionFetcherInitializer
import me.proton.core.userrecovery.presentation.compose.DeviceRecoveryInitializer

internal class AccountCoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {}

    override fun dependencies(): List<Class<out Initializer<*>?>?> = listOf(
        CryptoValidatorInitializer::class.java,
        DeviceRecoveryInitializer::class.java,
        HumanVerificationInitializer::class.java,
        MissingScopeInitializer::class.java,
        UnAuthSessionFetcherInitializer::class.java
    )

}
