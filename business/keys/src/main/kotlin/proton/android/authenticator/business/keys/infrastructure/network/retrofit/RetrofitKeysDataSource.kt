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

package proton.android.authenticator.business.keys.infrastructure.network.retrofit

import proton.android.authenticator.business.keys.infrastructure.network.CreateKeyRequestDto
import proton.android.authenticator.business.keys.infrastructure.network.KeyResponseDto
import proton.android.authenticator.business.keys.infrastructure.network.KeysResponseDto
import proton.android.authenticator.business.shared.domain.infrastructure.network.NetworkDataSource
import proton.android.authenticator.business.shared.domain.infrastructure.network.NetworkDataSource.Companion.ROOT_PATH
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface RetrofitKeysDataSource : NetworkDataSource {

    @POST("$ROOT_PATH/key")
    suspend fun createKey(@Body request: CreateKeyRequestDto): KeyResponseDto

    @GET("$ROOT_PATH/key")
    suspend fun getKeys(): KeysResponseDto

}
