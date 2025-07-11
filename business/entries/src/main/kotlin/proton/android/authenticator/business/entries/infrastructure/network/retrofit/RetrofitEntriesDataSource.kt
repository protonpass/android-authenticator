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

package proton.android.authenticator.business.entries.infrastructure.network.retrofit

import proton.android.authenticator.business.entries.infrastructure.network.CreateEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntriesResponseDto
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntryRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.CreateEntryResponseDto
import proton.android.authenticator.business.entries.infrastructure.network.DeleteEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.FetchEntriesResponseDto
import proton.android.authenticator.business.entries.infrastructure.network.SortEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntriesRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntriesResponseDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntryRequestDto
import proton.android.authenticator.business.entries.infrastructure.network.UpdateEntryResponseDto
import proton.android.authenticator.business.shared.domain.infrastructure.network.NetworkDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.network.NetworkDataSource.Companion.ROOT_PATH
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RetrofitEntriesDataSource : NetworkDataSource {

    @POST("$ROOT_PATH/entry")
    suspend fun createEntry(@Body request: CreateEntryRequestDto): CreateEntryResponseDto

    @POST("$ROOT_PATH/entry/bulk")
    suspend fun createEntries(@Body request: CreateEntriesRequestDto): CreateEntriesResponseDto

    @DELETE("$ROOT_PATH/entry/{entryId}")
    suspend fun deleteEntry(@Path("entryId") entryId: String)

    @HTTP(method = "DELETE", path = "$ROOT_PATH/entry/bulk", hasBody = true)
    suspend fun deleteEntries(@Body request: DeleteEntriesRequestDto)

    @GET("$ROOT_PATH/entry")
    suspend fun getEntries(@Query("Since") lastId: String?): FetchEntriesResponseDto

    @PUT("$ROOT_PATH/entry/order")
    suspend fun sortEntries(@Body request: SortEntriesRequestDto)

    @PUT("$ROOT_PATH/entry/{entryId}")
    suspend fun updateEntry(
        @Path("entryId") entryId: String,
        @Body request: UpdateEntryRequestDto
    ): UpdateEntryResponseDto

    @PUT("$ROOT_PATH/entry/bulk")
    suspend fun updateEntries(@Body request: UpdateEntriesRequestDto): UpdateEntriesResponseDto

}
