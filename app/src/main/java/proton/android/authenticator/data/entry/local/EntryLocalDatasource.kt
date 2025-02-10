package proton.android.authenticator.data.entry.local

import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.data.AuthenticatorDatabase
import proton.android.authenticator.data.entry.EntryEntity
import javax.inject.Inject

interface EntryLocalDataSource {
    fun observeAllEntries(): Flow<List<EntryEntity>>
    suspend fun insertEntry(vararg entry: EntryEntity)
}

class EntryLocalDataSourceImpl @Inject constructor(
    private val database: AuthenticatorDatabase
) : EntryLocalDataSource {

    override fun observeAllEntries(): Flow<List<EntryEntity>> = database.entryDao().observeAll()

    override suspend fun insertEntry(vararg entry: EntryEntity) = database.entryDao().insertOrUpdate(*entry)
}
