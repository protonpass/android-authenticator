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
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.proton.core.presentation.app.AppLifecycleProvider
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase

internal class DefaultNightModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        with(
            receiver = EntryPointAccessors.fromApplication(
                context.applicationContext,
                DefaultNightModeInitializerDependencies::class.java
            )
        ) {
            getSettingsObserver().invoke()
                .onEach(::setDefaultNighMode)
                .flowWithLifecycle(getAppLifecycleProvider().lifecycle)
                .launchIn(getAppLifecycleProvider().lifecycle.coroutineScope)
        }
    }

    private fun setDefaultNighMode(settings: Settings) {
        when (settings.themeType) {
            SettingsThemeType.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            SettingsThemeType.Light -> AppCompatDelegate.MODE_NIGHT_NO
            SettingsThemeType.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }.also(AppCompatDelegate::setDefaultNightMode)
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    @[EntryPoint InstallIn(SingletonComponent::class)]
    internal interface DefaultNightModeInitializerDependencies {

        fun getAppLifecycleProvider(): AppLifecycleProvider

        fun getSettingsObserver(): ObserveSettingsUseCase

    }

}
