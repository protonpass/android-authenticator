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

package proton.android.authenticator.business.keys.infrastructure

import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.business.keys.domain.KeysRepository
import proton.android.authenticator.business.shared.domain.infrastructure.persistence.PersistenceDataSource
import javax.inject.Inject

internal class KeysRepositoryImpl @Inject constructor(
    private val localDataSource: PersistenceDataSource<Key>
) : KeysRepository {

    override fun findAll(): Flow<List<Key>> = localDataSource.observeAll()

    override suspend fun save(key: Key) {
        localDataSource.insert(key)
    }

    override suspend fun saveAll(keys: List<Key>) {
        localDataSource.insertAll(keys)
    }

}
