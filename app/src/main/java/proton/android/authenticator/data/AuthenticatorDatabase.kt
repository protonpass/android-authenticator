package proton.android.authenticator.data

import me.proton.core.data.room.db.Database
import proton.android.authenticator.data.entry.EntryDao

interface AuthenticatorDatabase : Database {
    fun entryDao(): EntryDao
}
