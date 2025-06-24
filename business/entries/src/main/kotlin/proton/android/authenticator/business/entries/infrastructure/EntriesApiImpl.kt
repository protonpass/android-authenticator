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

package proton.android.authenticator.business.entries.infrastructure

import me.proton.core.domain.entity.UserId
import me.proton.core.network.data.ApiProvider
import proton.android.authenticator.business.entries.domain.EntriesApi
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.infrastructure.network.retrofit.RetrofitEntriesDataSource
import javax.inject.Inject

internal class EntriesApiImpl @Inject constructor(private val apiProvider: ApiProvider) : EntriesApi {

    override suspend fun fetchAll(userId: String): List<Entry> = apiProvider
        .get<RetrofitEntriesDataSource>(userId = UserId(id = userId))
        .invoke { getEntries() }
        .valueOrThrow
        .let { response ->
            println("JIBIRI: response: $response")
            emptyList()
        }

}
