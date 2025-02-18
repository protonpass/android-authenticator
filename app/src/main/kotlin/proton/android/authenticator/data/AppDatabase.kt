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

package proton.android.authenticator.data

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import me.proton.core.crypto.android.keystore.CryptoConverters
import me.proton.core.data.room.db.BaseDatabase
import me.proton.core.data.room.db.CommonConverters
import proton.android.authenticator.data.converters.AuthenticatorConverters
import proton.android.authenticator.data.entry.EntryEntity

@Database(
    entities = [
        EntryEntity::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = false
)
@TypeConverters(
    // Core
    CommonConverters::class,
    CryptoConverters::class,
    // Authenticator
    AuthenticatorConverters::class
)
abstract class AppDatabase : BaseDatabase(), AuthenticatorDatabase {

    companion object {
        const val VERSION = 1

        private const val DB_NAME = "db-authenticator"

        fun buildDatabase(context: Context): AppDatabase = databaseBuilder<AppDatabase>(context, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}
