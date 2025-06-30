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

package proton.android.authenticator.app.di

import android.content.Context
import androidx.work.WorkManager
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.proton.core.account.domain.entity.AccountType
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.auth.domain.usecase.PostLoginAccountSetup
import me.proton.core.auth.presentation.DefaultUserCheck
import me.proton.core.auth.presentation.HelpOptionHandler
import me.proton.core.domain.entity.AppStore
import me.proton.core.domain.entity.Product
import me.proton.core.eventmanager.domain.EventListener
import me.proton.core.notification.data.NotificationEventListener
import me.proton.core.plan.domain.ProductOnlyPaidPlans
import me.proton.core.plan.domain.SupportSignupPaidPlans
import me.proton.core.plan.domain.SupportUpgradePaidPlans
import me.proton.core.push.data.PushEventListener
import me.proton.core.user.data.UserAddressEventListener
import me.proton.core.user.data.UserEventListener
import me.proton.core.user.domain.UserManager
import me.proton.core.usersettings.data.UserSettingsEventListener
import proton.android.authenticator.app.configs.AuthenticatorAppConfig
import proton.android.authenticator.app.handler.RequestReviewHandler
import proton.android.authenticator.app.handler.RequestReviewHandlerImpl
import proton.android.authenticator.shared.common.domain.configs.AppConfig
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class ApplicationModule {

    @[Binds Singleton]
    internal abstract fun bindAppConfig(impl: AuthenticatorAppConfig): AppConfig

    internal companion object {

        private const val IMAGE_LOADER_DISK_CACHE_DIR = "image_cache"
        private const val IMAGE_LOADER_DISK_CACHE_MAX_SIZE = 0.02
        private const val IMAGE_LOADER_MEMORY_CACHE_MAX_SIZE = 0.25

        @[Provides Singleton]
        internal fun provideAccountType(config: AppConfig): AccountType = config.accountType

        @[Provides Singleton]
        internal fun provideAppStore(config: AppConfig): AppStore = config.appStore

        @[Provides Singleton]
        internal fun provideHelpOptionHandler(config: AppConfig): HelpOptionHandler = config.helpOptionHandler

        @[Provides Singleton]
        internal fun provideProduct(config: AppConfig): Product = config.product

        @[Provides ProductOnlyPaidPlans]
        internal fun provideProductOnlyPaidPlans(config: AppConfig) = config.productOnlyPaidPlans

        @[Provides SupportSignupPaidPlans]
        internal fun provideSupportSignupPaidPlans(config: AppConfig) = config.supportSignupPaidPlans

        @[Provides SupportUpgradePaidPlans]
        internal fun provideSupportUpgradePaidPlans(config: AppConfig) = config.supportUpgradePaidPlans

        @[Provides Singleton]
        internal fun provideUserCheck(
            @ApplicationContext context: Context,
            accountManager: AccountManager,
            userManager: UserManager
        ): PostLoginAccountSetup.UserCheck = DefaultUserCheck(context, accountManager, userManager)

        @[Provides Singleton ElementsIntoSet JvmSuppressWildcards]
        internal fun provideEventListenerSet(
            notificationEventListener: NotificationEventListener,
            pushEventListener: PushEventListener,
            userAddressEventListener: UserAddressEventListener,
            userEventListener: UserEventListener,
            userSettingsEventListener: UserSettingsEventListener
        ): Set<EventListener<*, *>> = setOf(
            notificationEventListener,
            pushEventListener,
            userAddressEventListener,
            userEventListener,
            userSettingsEventListener
        )

        @[Provides Singleton ApplicationCoroutineScope]
        internal fun provideCoroutineScope(): CoroutineScope = MainScope()

        @[Provides Singleton]
        internal fun provideImageLoader(@ApplicationContext context: Context): ImageLoader =
            ImageLoader.Builder(context)
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve(IMAGE_LOADER_DISK_CACHE_DIR))
                        .maxSizePercent(IMAGE_LOADER_DISK_CACHE_MAX_SIZE)
                        .build()
                }
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(IMAGE_LOADER_MEMORY_CACHE_MAX_SIZE)
                        .build()
                }
                .build()

        @[Provides Singleton]
        internal fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @[Provides Singleton]
        internal fun bindRequestReviewHandler(@ApplicationContext context: Context):
                RequestReviewHandler = RequestReviewHandlerImpl(context)

    }

}
