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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EntriesDao {

    @Query("SELECT * FROM ${EntryEntity.TABLE}")
    fun observeAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM ${EntryEntity.TABLE} WHERE id = :id")
    fun observeById(id: String): Flow<EntryEntity>

    @Delete
    suspend fun delete(entryEntity: EntryEntity)

    @Upsert
    suspend fun upsert(entryEntity: EntryEntity)

    @Upsert
    suspend fun upsertAll(entryEntities: List<EntryEntity>)

}
