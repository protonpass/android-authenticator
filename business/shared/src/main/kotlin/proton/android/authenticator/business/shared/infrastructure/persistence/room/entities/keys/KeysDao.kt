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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface KeysDao {

    @Query("SELECT * FROM ${KeyEntity.TABLE}")
    fun observeAll(): Flow<List<KeyEntity>>

    @Query("SELECT * FROM ${KeyEntity.TABLE} WHERE id = :id")
    fun observeById(id: String): Flow<KeyEntity>

    @Delete
    suspend fun delete(keyEntity: KeyEntity)

    @Upsert
    suspend fun upsert(keyEntity: KeyEntity)

    @Upsert
    suspend fun upsertAll(entryEntities: List<KeyEntity>)

}
