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
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.proton.core.crypto.android.keystore.CryptoConverters
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntryEntity

@Database(
    entities = [
        EntryEntity::class
    ],
    exportSchema = true,
    version = AuthenticatorDatabase.VERSION
)
@TypeConverters(
    value = [
        CryptoConverters::class
    ]
)
internal abstract class AuthenticatorDatabase : RoomDatabase() {

    internal abstract fun entriesDao(): EntriesDao

    internal companion object {

        internal const val NAME = "authenticator.db"

        internal const val VERSION = 1

    }

}
