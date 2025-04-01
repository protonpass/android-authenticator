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

package proton.android.authenticator.shared.common.checkers

import android.content.pm.PackageManager
import android.os.Build
import proton.android.authenticator.shared.common.domain.checkers.AppInstalledChecker
import proton.android.authenticator.shared.common.domain.dispatchers.AppDispatchers
import javax.inject.Inject

internal class AndroidAppInstalledChecker @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val packageManager: PackageManager
) : AppInstalledChecker {

    override suspend fun check(packageName: String): Boolean = with(appDispatchers.io) {
        try {
            PackageManager.GET_META_DATA
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                packageManager.getApplicationInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

}
