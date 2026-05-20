/*
 * Copyright (c) 2026 Proton AG
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

package proton.android.authenticator.features.shared.usecases.featureflag

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import proton.android.authenticator.shared.common.domain.configs.AppConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlagOverridesRepository @Inject constructor(
    @ApplicationContext context: Context,
    appConfig: AppConfig
) {

    private val areOverridesAllowed: Boolean = appConfig.buildFlavor.type.canDisplayDebugScreen

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _overrides = MutableStateFlow(loadAll())
    val overrides: StateFlow<Map<FeatureFlag, Boolean>> = _overrides.asStateFlow()

    fun get(flag: FeatureFlag): Boolean? = _overrides.value[flag]

    fun set(flag: FeatureFlag, value: Boolean) {
        if (!areOverridesAllowed) return
        synchronized(this) {
            prefs.edit { putBoolean(flag.key, value) }
            _overrides.value = loadAll()
        }
    }

    private fun loadAll(): Map<FeatureFlag, Boolean> = if (!areOverridesAllowed) {
        emptyMap()
    } else {
        FeatureFlag.entries
            .filter { prefs.contains(it.key) }
            .associateWith { prefs.getBoolean(it.key, false) }
    }

    private companion object {

        private const val PREFS_NAME = "feature_flag_overrides"

    }

}
