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

package proton.android.authenticator.business.keys.infrastructure.persistence.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys.KeyEntity
import proton.android.authenticator.business.shared.infrastructure.persistence.room.entities.keys.KeysDao
import javax.inject.Inject

internal class RoomKeysPersistenceDataSource @Inject constructor(
    private val keysDao: KeysDao
) : PersistenceDataSource<Key> {

    override fun observeAll(): Flow<List<Key>> = keysDao.observeAll()
        .map { keyEntities -> keyEntities.map(KeyEntity::toDomain) }

    override fun byId(id: String): Flow<Key> = keysDao.observeById(id = id)
        .map(KeyEntity::toDomain)

    override suspend fun delete(key: Key) {
        keysDao.delete(key.toEntity())
    }

    override suspend fun deleteAll(keys: List<Key>) {
        keys.map(Key::toEntity)
            .also { keyEntities -> keysDao.deleteAll(keyEntities) }
    }

    override suspend fun insert(key: Key) {
        keysDao.upsert(key.toEntity())
    }

    override suspend fun insertAll(keys: List<Key>) {
        keys.map(Key::toEntity)
            .also { keyEntities -> keysDao.upsertAll(keyEntities) }
    }

    override suspend fun searchMaxPosition(): Int = 0

}

private fun Key.toEntity() = KeyEntity(
    id = id,
    key = key,
    userId = userId,
    userKeyId = userKeyId,
    symmetricallyEncryptedKey = encryptedKey
)

private fun KeyEntity.toDomain() = Key(
    id = id,
    key = key,
    userId = userId,
    userKeyId = userKeyId,
    encryptedKey = symmetricallyEncryptedKey
)
