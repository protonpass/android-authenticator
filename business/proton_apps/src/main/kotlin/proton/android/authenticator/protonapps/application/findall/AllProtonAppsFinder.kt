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

package proton.android.authenticator.protonapps.application.findall

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import proton.android.authenticator.protonapps.domain.ProtonApp
import proton.android.authenticator.protonapps.domain.ProtonAppType
import javax.inject.Inject

internal class AllProtonAppsFinder @Inject constructor(@ApplicationContext private val context: Context) {

    internal fun findAll(): Flow<List<ProtonApp>> = ProtonAppType.entries.map { protonAppType ->
        try {
            context.packageManager.let { pm ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getApplicationInfo(
                        protonAppType.id,
                        PackageManager.ApplicationInfoFlags.of(0)
                    )
                } else {
                    pm.getApplicationInfo(protonAppType.id, 0)
                }
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }.let { isInstalled ->
            ProtonApp(
                type = protonAppType,
                isInstalled = isInstalled
            )
        }
    }.let(::flowOf)

}
