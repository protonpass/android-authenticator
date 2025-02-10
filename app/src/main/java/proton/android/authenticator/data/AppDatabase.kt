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
