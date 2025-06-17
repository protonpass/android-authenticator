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

package proton.android.authenticator.business.shared.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import me.proton.core.account.data.db.AccountDatabase
import me.proton.core.account.domain.entity.AccountType
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.auth.data.db.AuthDatabase
import me.proton.core.auth.domain.usecase.PostLoginAccountSetup
import me.proton.core.auth.presentation.DefaultHelpOptionHandler
import me.proton.core.auth.presentation.DefaultUserCheck
import me.proton.core.auth.presentation.HelpOptionHandler
import me.proton.core.challenge.data.db.ChallengeDatabase
import me.proton.core.domain.entity.AppStore
import me.proton.core.domain.entity.Product
import me.proton.core.eventmanager.data.db.EventMetadataDatabase
import me.proton.core.eventmanager.domain.EventListener
import me.proton.core.featureflag.data.db.FeatureFlagDatabase
import me.proton.core.humanverification.data.db.HumanVerificationDatabase
import me.proton.core.humanverification.presentation.HumanVerificationApiHost
import me.proton.core.humanverification.presentation.utils.HumanVerificationVersion
import me.proton.core.key.data.db.KeySaltDatabase
import me.proton.core.key.data.db.PublicAddressDatabase
import me.proton.core.keytransparency.data.local.KeyTransparencyDatabase
import me.proton.core.network.data.client.ExtraHeaderProviderImpl
import me.proton.core.network.data.di.AlternativeApiPins
import me.proton.core.network.data.di.BaseProtonApiUrl
import me.proton.core.network.data.di.CertificatePins
import me.proton.core.network.data.di.DohProviderUrls
import me.proton.core.network.domain.ApiClient
import me.proton.core.network.domain.client.ExtraHeaderProvider
import me.proton.core.network.domain.serverconnection.DohAlternativesListener
import me.proton.core.notification.data.NotificationEventListener
import me.proton.core.notification.data.local.db.NotificationDatabase
import me.proton.core.observability.data.db.ObservabilityDatabase
import me.proton.core.payment.data.local.db.PaymentDatabase
import me.proton.core.push.data.PushEventListener
import me.proton.core.push.data.local.db.PushDatabase
import me.proton.core.telemetry.data.db.TelemetryDatabase
import me.proton.core.telemetry.data.repository.TelemetryLocalDataSourceImpl
import me.proton.core.telemetry.data.repository.TelemetryRemoteDataSourceImpl
import me.proton.core.telemetry.data.repository.TelemetryRepositoryImpl
import me.proton.core.telemetry.data.usecase.IsTelemetryEnabledImpl
import me.proton.core.telemetry.data.worker.TelemetryWorkerManagerImpl
import me.proton.core.telemetry.domain.TelemetryWorkerManager
import me.proton.core.telemetry.domain.repository.TelemetryLocalDataSource
import me.proton.core.telemetry.domain.repository.TelemetryRemoteDataSource
import me.proton.core.telemetry.domain.repository.TelemetryRepository
import me.proton.core.telemetry.domain.usecase.IsTelemetryEnabled
import me.proton.core.user.data.UserAddressEventListener
import me.proton.core.user.data.UserEventListener
import me.proton.core.user.data.db.AddressDatabase
import me.proton.core.user.data.db.UserDatabase
import me.proton.core.user.domain.UserManager
import me.proton.core.userrecovery.data.db.DeviceRecoveryDatabase
import me.proton.core.usersettings.data.UserSettingsEventListener
import me.proton.core.usersettings.data.db.OrganizationDatabase
import me.proton.core.usersettings.data.db.UserSettingsDatabase
import okhttp3.HttpUrl
import proton.android.authenticator.business.shared.app.AuthenticatorAppConfig
import proton.android.authenticator.business.shared.domain.app.AppConfig
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryCreator
import proton.android.authenticator.business.shared.domain.infrastructure.directories.DirectoryReader
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileDeleter
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileReader
import proton.android.authenticator.business.shared.domain.infrastructure.files.FileWriter
import proton.android.authenticator.business.shared.domain.network.NetworkConfig
import proton.android.authenticator.business.shared.infrastructure.directories.InternalDirectoryCreator
import proton.android.authenticator.business.shared.infrastructure.directories.InternalDirectoryReader
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileReader
import proton.android.authenticator.business.shared.infrastructure.files.ContentResolverFileWriter
import proton.android.authenticator.business.shared.infrastructure.files.InternalFileDeleter
import proton.android.authenticator.business.shared.infrastructure.files.InternalFileWriter
import proton.android.authenticator.business.shared.infrastructure.network.AuthenticatorApiClient
import proton.android.authenticator.business.shared.infrastructure.network.AuthenticatorNetworkConfig
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.backups.BackupProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.settings.SettingsProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.steps.StepProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.room.AuthenticatorDatabase
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.proto.preferences.backups.BackupPreferences
import proton.android.authenticator.proto.preferences.settings.SettingsPreferences
import proton.android.authenticator.proto.preferences.steps.StepPreferences
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class) Suppress("TooManyFunctions")]
internal abstract class BusinessSharedModule {

    @[Binds Singleton]
    internal abstract fun bindApiClient(impl: AuthenticatorApiClient): ApiClient

    @[Binds Singleton]
    internal abstract fun bindAppConfig(impl: AuthenticatorAppConfig): AppConfig

    @[Binds Singleton]
    internal abstract fun bindNetworkConfig(impl: AuthenticatorNetworkConfig): NetworkConfig

    @[Binds Singleton]
    internal abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository

    @[Binds Singleton]
    internal abstract fun bindTelemetryLocalDataSource(impl: TelemetryLocalDataSourceImpl): TelemetryLocalDataSource

    @[Binds Singleton]
    internal abstract fun bindTelemetryRemoteDataSource(impl: TelemetryRemoteDataSourceImpl): TelemetryRemoteDataSource

    @[Binds Singleton]
    internal abstract fun bindIsTelemetryEnabled(impl: IsTelemetryEnabledImpl): IsTelemetryEnabled

    @[Binds Singleton]
    internal abstract fun bindTelemetryWorkerManager(impl: TelemetryWorkerManagerImpl): TelemetryWorkerManager

    @[Binds Singleton]
    internal abstract fun bindAccountDatabase(impl: AuthenticatorDatabase): AccountDatabase

    @[Binds Singleton]
    internal abstract fun bindAddressDatabase(impl: AuthenticatorDatabase): AddressDatabase

    @[Binds Singleton]
    internal abstract fun bindAuthDatabase(impl: AuthenticatorDatabase): AuthDatabase

    @[Binds Singleton]
    internal abstract fun bindChallengeDatabase(impl: AuthenticatorDatabase): ChallengeDatabase

    @[Binds Singleton]
    internal abstract fun bindDeviceRecoveryDatabase(impl: AuthenticatorDatabase): DeviceRecoveryDatabase

    @[Binds Singleton]
    internal abstract fun bindEventMetadataDatabase(impl: AuthenticatorDatabase): EventMetadataDatabase

    @[Binds Singleton]
    internal abstract fun bindFeatureFlagDatabase(impl: AuthenticatorDatabase): FeatureFlagDatabase

    @[Binds Singleton]
    internal abstract fun bindHumanVerificationDatabase(impl: AuthenticatorDatabase): HumanVerificationDatabase

    @[Binds Singleton]
    internal abstract fun bindKeySaltDatabase(impl: AuthenticatorDatabase): KeySaltDatabase

    @[Binds Singleton]
    internal abstract fun bindKeyTransparencyDatabase(impl: AuthenticatorDatabase): KeyTransparencyDatabase

    @[Binds Singleton]
    internal abstract fun bindNotificationDatabase(impl: AuthenticatorDatabase): NotificationDatabase

    @[Binds Singleton]
    internal abstract fun bindObservabilityDatabase(impl: AuthenticatorDatabase): ObservabilityDatabase

    @[Binds Singleton]
    internal abstract fun bindOrganizationDatabase(impl: AuthenticatorDatabase): OrganizationDatabase

    @[Binds Singleton]
    internal abstract fun bindPaymentDatabase(impl: AuthenticatorDatabase): PaymentDatabase

    @[Binds Singleton]
    internal abstract fun bindPublicAddressDatabase(impl: AuthenticatorDatabase): PublicAddressDatabase

    @[Binds Singleton]
    internal abstract fun bindPushDatabase(impl: AuthenticatorDatabase): PushDatabase

    @[Binds Singleton]
    internal abstract fun bindTelemetryDatabase(impl: AuthenticatorDatabase): TelemetryDatabase

    @[Binds Singleton]
    internal abstract fun bindUserDatabase(impl: AuthenticatorDatabase): UserDatabase

    @[Binds Singleton]
    internal abstract fun bindUserSettingsDatabase(impl: AuthenticatorDatabase): UserSettingsDatabase

    @[Binds Singleton]
    internal abstract fun bindDirectoryCreator(impl: InternalDirectoryCreator): DirectoryCreator

    @[Binds Singleton]
    internal abstract fun bindDirectoryReader(impl: InternalDirectoryReader): DirectoryReader

    @[Binds Singleton]
    internal abstract fun bindFileReader(impl: ContentResolverFileReader): FileReader

    @[Binds Singleton FileWriterContentResolver]
    internal abstract fun bindContentResolverFileWriter(impl: ContentResolverFileWriter): FileWriter

    @[Binds Singleton FileDeleterInternal]
    internal abstract fun bindInternalFileDeleter(impl: InternalFileDeleter): FileDeleter

    @[Binds Singleton FileWriterInternal]
    internal abstract fun bindInternalFileWriter(impl: InternalFileWriter): FileWriter

    internal companion object {

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

        @[Provides Singleton]
        internal fun provideUserCheck(
            @ApplicationContext context: Context,
            accountManager: AccountManager,
            userManager: UserManager
        ): PostLoginAccountSetup.UserCheck = DefaultUserCheck(context, accountManager, userManager)

        @[Provides Singleton]
        internal fun provideHelpOptionHandler(): HelpOptionHandler = DefaultHelpOptionHandler()

        @[Provides Singleton]
        internal fun provideAccountType(config: AppConfig): AccountType = config.accountType

        @[Provides Singleton]
        internal fun provideProduct(config: AppConfig): Product = config.product

        @[Provides Singleton]
        internal fun provideAppStore(config: AppConfig): AppStore = config.appStore

        @[Provides BaseProtonApiUrl]
        internal fun provideProtonApiUrl(config: NetworkConfig): HttpUrl = config.hostHttpUrl

        @[Provides Singleton]
        internal fun provideExtraHeaderProvider(config: NetworkConfig): ExtraHeaderProvider =
            ExtraHeaderProviderImpl().apply { addHeaders(*config.headers) }

        @[Provides Singleton DohProviderUrls]
        internal fun provideDohProviderUrls(config: NetworkConfig): Array<String> = config.dohProvidersUrls

        @[Provides Singleton CertificatePins]
        internal fun provideCertificatePins(config: NetworkConfig): Array<String> = config.certificatePins

        @[Provides Singleton AlternativeApiPins]
        internal fun provideAlternativeApiPins(config: NetworkConfig): List<String> = config.alternativeApiPins

        @[Provides Singleton]
        internal fun provideDohAlternativesListener(): DohAlternativesListener? = null

        @[Provides Singleton]
        internal fun provideHumanVerificationVersion(config: NetworkConfig): HumanVerificationVersion =
            config.humanVerificationVersion

        @[Provides HumanVerificationApiHost]
        internal fun provideHumanVerificationApiHost(config: NetworkConfig): String = config.humanVerificationHost

        @[Provides Singleton DirectoryPathInternal]
        internal fun provideDirectoryPath(@ApplicationContext context: Context): String = context
            .filesDir
            .absolutePath

        @[Provides Singleton]
        internal fun provideUsersDao(database: AuthenticatorDatabase): EntriesDao = database.entriesDao()

        @[Provides Singleton]
        internal fun provideAuthenticatorDatabase(@ApplicationContext context: Context): AuthenticatorDatabase =
            Room.databaseBuilder(
                context = context,
                klass = AuthenticatorDatabase::class.java,
                name = AuthenticatorDatabase.NAME
            )
                .fallbackToDestructiveMigration()
                .build()

        @[Provides Singleton]
        internal fun provideBackupPreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<BackupPreferences> = DataStoreFactory.create(
            serializer = BackupProtoPreferencesSerializer,
            produceFile = { context.dataStoreFile("backup_preferences.pb") }
        )

        @[Provides Singleton]
        internal fun provideSettingsPreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<SettingsPreferences> = DataStoreFactory.create(
            serializer = SettingsProtoPreferencesSerializer,
            produceFile = { context.dataStoreFile("settings_preferences.pb") }
        )

        @[Provides Singleton]
        internal fun provideStepsPreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<StepPreferences> = DataStoreFactory.create(
            serializer = StepProtoPreferencesSerializer,
            produceFile = { context.dataStoreFile("step_preferences.pb") }
        )

    }

}
