package proton.android.authenticator.data.entry

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.proton.core.data.room.db.BaseDao

@Dao
abstract class EntryDao : BaseDao<EntryEntity>() {

    @Query("SELECT * FROM ${EntryEntity.TABLE}")
    abstract fun observeAll(): Flow<List<EntryEntity>>
}
