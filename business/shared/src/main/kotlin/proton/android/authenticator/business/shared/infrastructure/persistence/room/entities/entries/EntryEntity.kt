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

package proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import me.proton.core.crypto.common.keystore.EncryptedByteArray

@Entity(
    tableName = EntryEntity.TABLE,
    primaryKeys = [EntryEntity.Columns.ID]
)
data class EntryEntity(
    @ColumnInfo(name = Columns.ID)
    val id: String,
    @ColumnInfo(name = Columns.ENCRYPTED_CONTENT)
    val content: EncryptedByteArray,
    @ColumnInfo(name = Columns.IS_SYNCED)
    val isSynced: Boolean,
    @ColumnInfo(name = Columns.POSITION)
    val position: Double,
    @ColumnInfo(name = Columns.CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = Columns.MODIFIED_AT)
    val modifiedAt: Long
) {

    internal object Columns {

        internal const val ID = "id"

        internal const val ENCRYPTED_CONTENT = "encrypted_content"

        internal const val IS_SYNCED = "is_synced"

        internal const val POSITION = "position"

        internal const val CREATED_AT = "created_at"

        internal const val MODIFIED_AT = "modified_at"

    }

    internal companion object {

        internal const val TABLE = "EntryEntity"

    }

}
