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

package proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import me.proton.core.crypto.common.keystore.EncryptedByteArray
import me.proton.core.user.data.entity.UserEntity
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.shared.SharedColumns

@Entity(
    tableName = KeyEntity.TABLE,
    primaryKeys = [
        KeyEntity.Columns.ID
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [SharedColumns.USER_ID],
            childColumns = [KeyEntity.Columns.USER_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class KeyEntity(
    @ColumnInfo(name = Columns.ID)
    val id: String,
    @ColumnInfo(name = Columns.KEY)
    val key: String,
    @ColumnInfo(name = Columns.SYMMETRICALLY_ENCRYPTED_KEY)
    val symmetricallyEncryptedKey: EncryptedByteArray,
    @ColumnInfo(name = Columns.USER_ID, index = true)
    val userId: String,
    @ColumnInfo(name = Columns.USER_KEY_ID)
    val userKeyId: String
) {

    internal object Columns {

        internal const val ID = "id"

        internal const val KEY = "key"

        internal const val SYMMETRICALLY_ENCRYPTED_KEY = "symmetrically_encrypted_key"

        internal const val USER_ID = "user_id"

        internal const val USER_KEY_ID = "user_key_id"

    }

    internal companion object {

        internal const val TABLE = "KeyEntity"

    }

}
