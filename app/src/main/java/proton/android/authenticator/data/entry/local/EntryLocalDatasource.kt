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
