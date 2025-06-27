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

package proton.android.authenticator.business.shared.infrastructure.persistence.room

import androidx.room.Database
import androidx.room.TypeConverters
import me.proton.core.account.data.db.AccountConverters
import me.proton.core.account.data.db.AccountDatabase
import me.proton.core.account.data.entity.AccountEntity
import me.proton.core.account.data.entity.AccountMetadataEntity
import me.proton.core.account.data.entity.SessionDetailsEntity
import me.proton.core.account.data.entity.SessionEntity
import me.proton.core.auth.data.db.AuthConverters
import me.proton.core.auth.data.db.AuthDatabase
import me.proton.core.auth.data.entity.AuthDeviceEntity
import me.proton.core.auth.data.entity.DeviceSecretEntity
import me.proton.core.auth.data.entity.MemberDeviceEntity
import me.proton.core.challenge.data.db.ChallengeConverters
import me.proton.core.challenge.data.db.ChallengeDatabase
import me.proton.core.challenge.data.entity.ChallengeFrameEntity
import me.proton.core.crypto.android.keystore.CryptoConverters
import me.proton.core.data.room.db.BaseDatabase
import me.proton.core.data.room.db.CommonConverters
import me.proton.core.eventmanager.data.db.EventManagerConverters
import me.proton.core.eventmanager.data.db.EventMetadataDatabase
import me.proton.core.eventmanager.data.entity.EventMetadataEntity
import me.proton.core.featureflag.data.db.FeatureFlagDatabase
import me.proton.core.featureflag.data.entity.FeatureFlagEntity
import me.proton.core.humanverification.data.db.HumanVerificationConverters
import me.proton.core.humanverification.data.db.HumanVerificationDatabase
import me.proton.core.humanverification.data.entity.HumanVerificationEntity
import me.proton.core.key.data.db.KeySaltDatabase
import me.proton.core.key.data.db.PublicAddressDatabase
import me.proton.core.key.data.entity.KeySaltEntity
import me.proton.core.key.data.entity.PublicAddressEntity
import me.proton.core.key.data.entity.PublicAddressInfoEntity
import me.proton.core.key.data.entity.PublicAddressKeyDataEntity
import me.proton.core.key.data.entity.PublicAddressKeyEntity
import me.proton.core.keytransparency.data.local.KeyTransparencyDatabase
import me.proton.core.keytransparency.data.local.entity.AddressChangeEntity
import me.proton.core.keytransparency.data.local.entity.SelfAuditResultEntity
import me.proton.core.notification.data.local.db.NotificationConverters
import me.proton.core.notification.data.local.db.NotificationDatabase
import me.proton.core.notification.data.local.db.NotificationEntity
import me.proton.core.observability.data.db.ObservabilityDatabase
import me.proton.core.observability.data.entity.ObservabilityEventEntity
import me.proton.core.payment.data.local.db.PaymentDatabase
import me.proton.core.payment.data.local.entity.GooglePurchaseEntity
import me.proton.core.payment.data.local.entity.PurchaseEntity
import me.proton.core.push.data.local.db.PushConverters
import me.proton.core.push.data.local.db.PushDatabase
import me.proton.core.push.data.local.db.PushEntity
import me.proton.core.telemetry.data.db.TelemetryDatabase
import me.proton.core.telemetry.data.entity.TelemetryEventEntity
import me.proton.core.user.data.db.AddressDatabase
import me.proton.core.user.data.db.UserConverters
import me.proton.core.user.data.db.UserDatabase
import me.proton.core.user.data.entity.AddressEntity
import me.proton.core.user.data.entity.AddressKeyEntity
import me.proton.core.user.data.entity.UserEntity
import me.proton.core.user.data.entity.UserKeyEntity
import me.proton.core.userrecovery.data.db.DeviceRecoveryDatabase
import me.proton.core.userrecovery.data.entity.RecoveryFileEntity
import me.proton.core.usersettings.data.db.OrganizationDatabase
import me.proton.core.usersettings.data.db.UserSettingsConverters
import me.proton.core.usersettings.data.db.UserSettingsDatabase
import me.proton.core.usersettings.data.entity.OrganizationEntity
import me.proton.core.usersettings.data.entity.OrganizationKeysEntity
import me.proton.core.usersettings.data.entity.UserSettingsEntity
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntryEntity
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys.KeyEntity
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys.KeysDao

@Database(
    entities = [
        AccountEntity::class,
        AccountMetadataEntity::class,
        AddressChangeEntity::class,
        AddressEntity::class,
        AddressKeyEntity::class,
        AuthDeviceEntity::class,
        ChallengeFrameEntity::class,
        DeviceSecretEntity::class,
        EntryEntity::class,
        EventMetadataEntity::class,
        FeatureFlagEntity::class,
        GooglePurchaseEntity::class,
        HumanVerificationEntity::class,
        KeyEntity::class,
        KeySaltEntity::class,
        MemberDeviceEntity::class,
        NotificationEntity::class,
        ObservabilityEventEntity::class,
        OrganizationEntity::class,
        OrganizationKeysEntity::class,
        PublicAddressEntity::class,
        PublicAddressInfoEntity::class,
        PublicAddressKeyDataEntity::class,
        PublicAddressKeyEntity::class,
        PurchaseEntity::class,
        PushEntity::class,
        RecoveryFileEntity::class,
        SelfAuditResultEntity::class,
        SessionDetailsEntity::class,
        SessionEntity::class,
        TelemetryEventEntity::class,
        UserEntity::class,
        UserKeyEntity::class,
        UserSettingsEntity::class
    ],
    exportSchema = true,
    version = AuthenticatorDatabase.VERSION
)
@TypeConverters(
    value = [
        AccountConverters::class,
        AuthConverters::class,
        ChallengeConverters::class,
        CommonConverters::class,
        CryptoConverters::class,
        EventManagerConverters::class,
        HumanVerificationConverters::class,
        NotificationConverters::class,
        PushConverters::class,
        UserConverters::class,
        UserSettingsConverters::class
    ]
)
internal abstract class AuthenticatorDatabase :
    BaseDatabase(),
    AccountDatabase,
    AddressDatabase,
    AuthDatabase,
    ChallengeDatabase,
    DeviceRecoveryDatabase,
    EventMetadataDatabase,
    FeatureFlagDatabase,
    HumanVerificationDatabase,
    KeySaltDatabase,
    KeyTransparencyDatabase,
    NotificationDatabase,
    ObservabilityDatabase,
    OrganizationDatabase,
    PaymentDatabase,
    PublicAddressDatabase,
    PushDatabase,
    TelemetryDatabase,
    UserDatabase,
    UserSettingsDatabase {

    internal abstract fun entriesDao(): EntriesDao

    internal abstract fun keysDao(): KeysDao

    internal companion object {

        internal const val NAME = "authenticator.db"

        internal const val VERSION = 1

    }

}
