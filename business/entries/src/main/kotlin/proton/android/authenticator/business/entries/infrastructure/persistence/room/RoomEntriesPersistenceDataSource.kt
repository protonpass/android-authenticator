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

package proton.android.authenticator.business.entries.infrastructure.persistence.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntriesDao
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.entries.EntryEntity
import javax.inject.Inject

internal class RoomEntriesPersistenceDataSource @Inject constructor(
    private val entriesDao: EntriesDao
) : PersistenceDataSource<Entry> {

    override fun observeAll(): Flow<List<Entry>> = entriesDao.observeAll()
        .map { entryEntities -> entryEntities.map(EntryEntity::toDomain) }

    override fun byId(id: String): Flow<Entry> = entriesDao.observeById(id)
        .map(EntryEntity::toDomain)

    override suspend fun delete(entry: Entry) {
        entry.toEntity()
            .also { entryEntity -> entriesDao.delete(entryEntity) }
    }

    override suspend fun insert(entry: Entry) {
        entry.toEntity()
            .also { entryEntity -> entriesDao.upsert(entryEntity) }
    }

    override suspend fun insertAll(entries: List<Entry>) {
        entries.map { entry -> entry.toEntity() }
            .also { entryEntities -> entriesDao.upsertAll(entryEntities) }
    }

    override suspend fun searchMaxPosition(): Double? = entriesDao.searchMaxPosition()

}

private fun Entry.toEntity() = EntryEntity(
    id = id,
    content = content,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    isSynced = isSynced,
    position = position,
    iconUrl = iconUrl
)

private fun EntryEntity.toDomain() = Entry(
    id = id,
    content = content,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    isSynced = isSynced,
    position = position,
    iconUrl = iconUrl
)
