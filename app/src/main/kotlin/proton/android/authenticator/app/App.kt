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

package proton.android.authenticator.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.startup.AppInitializer
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import proton.android.authenticator.app.initializers.BackupPeriodicWorkInitializer
import proton.android.authenticator.app.initializers.KeyWorkInitializer
import proton.android.authenticator.app.initializers.SyncPeriodicWorkInitializer
import proton.android.authenticator.app.initializers.SyncWorkInitializer
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.features.shared.usecases.applock.UpdateAppLockStateUseCase
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
internal class App : Application(), ImageLoaderFactory {

    @Inject
    internal lateinit var imageLoader: Provider<ImageLoader>

    @Inject
    internal lateinit var updateAppLockStateUseCase: UpdateAppLockStateUseCase

    override fun newImageLoader(): ImageLoader = imageLoader.get()

    override fun onCreate() {
        super.onCreate()

        val observer = AppLifecycleObserver(
            onForeground = {
                ProcessLifecycleOwner.get().lifecycleScope.launch {
                    updateAppLockStateUseCase(AppLockState.AUTHENTICATING)
                }
            },
            onBackground = {
                ProcessLifecycleOwner.get().lifecycleScope.launch {
                    updateAppLockStateUseCase(AppLockState.LOCKED)
                }
            }
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        initInitializerComponents()
    }

    private fun initInitializerComponents() {
        with(AppInitializer.getInstance(applicationContext)) {
            initializeComponent(BackupPeriodicWorkInitializer::class.java)
            initializeComponent(KeyWorkInitializer::class.java)
            initializeComponent(SyncPeriodicWorkInitializer::class.java)
            initializeComponent(SyncWorkInitializer::class.java)
        }
    }

}
