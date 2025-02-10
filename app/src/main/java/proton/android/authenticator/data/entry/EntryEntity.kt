package proton.android.authenticator.data.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import proton.android.authenticator.domain.EntryType

@Entity(
    tableName = EntryEntity.TABLE,
    indices = [
        Index(value = [EntryEntity.Columns.TYPE]),
        Index(value = [EntryEntity.Columns.CREATED_AT]),
        Index(value = [EntryEntity.Columns.MODIFIED_AT]),
        Index(value = [EntryEntity.Columns.TYPE, EntryEntity.Columns.CREATED_AT])
    ]
)
data class EntryEntity(
    @ColumnInfo(name = Columns.ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = Columns.ENCRYPTED_CONTENT)
    val content: EncryptedByteArray,
    @ColumnInfo(name = Columns.TYPE)
    val type: EntryType,
    @ColumnInfo(name = Columns.CREATED_AT)
    val createdAt: Instant,
    @ColumnInfo(name = Columns.MODIFIED_AT)
    val modifiedAt: Instant
) {
    object Columns {
        const val ID = "id"
        const val ENCRYPTED_CONTENT = "encrypted_content"
        const val TYPE = "type"
        const val CREATED_AT = "created_at"
        const val MODIFIED_AT = "modified_at"
    }

    companion object {
        const val TABLE = "EntryEntity"
    }
}

