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

package proton.android.authenticator.business.shared.infrastructure.persistence.di

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
import me.proton.core.account.data.db.AccountDatabase
import me.proton.core.auth.data.db.AuthDatabase
import me.proton.core.challenge.data.db.ChallengeDatabase
import me.proton.core.eventmanager.data.db.EventMetadataDatabase
import me.proton.core.featureflag.data.db.FeatureFlagDatabase
import me.proton.core.humanverification.data.db.HumanVerificationDatabase
import me.proton.core.key.data.db.KeySaltDatabase
import me.proton.core.key.data.db.PublicAddressDatabase
import me.proton.core.keytransparency.data.local.KeyTransparencyDatabase
import me.proton.core.notification.data.local.db.NotificationDatabase
import me.proton.core.observability.data.db.ObservabilityDatabase
import me.proton.core.payment.data.local.db.PaymentDatabase
import me.proton.core.push.data.local.db.PushDatabase
import me.proton.core.telemetry.data.db.TelemetryDatabase
import me.proton.core.user.data.db.AddressDatabase
import me.proton.core.user.data.db.UserDatabase
import me.proton.core.userrecovery.data.db.DeviceRecoveryDatabase
import me.proton.core.usersettings.data.db.OrganizationDatabase
import me.proton.core.usersettings.data.db.UserSettingsDatabase
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.backups.BackupProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.settings.SettingsProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.datastore.proto.steps.StepProtoPreferencesSerializer
import proton.android.authenticator.business.shared.infrastructure.persistence.room.AuthenticatorDatabase
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys.KeysDao
import proton.android.authenticator.proto.preferences.backups.BackupPreferences
import proton.android.authenticator.proto.preferences.settings.SettingsPreferences
import proton.android.authenticator.proto.preferences.steps.StepPreferences
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessSharedPersistenceModule {

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

    internal companion object {

        @[Provides Singleton]
        internal fun provideKeysDao(database: AuthenticatorDatabase): KeysDao = database.keysDao()

        @[Provides Singleton]
        internal fun provideEntriesDao(database: AuthenticatorDatabase): EntriesDao = database.entriesDao()

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
