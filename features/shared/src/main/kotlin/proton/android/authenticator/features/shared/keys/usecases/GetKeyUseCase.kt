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

package proton.android.authenticator.features.shared.keys.usecases

import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.keys.application.findall.FindAllKeysQuery
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.features.shared.users.usecases.ObserveUserUseCase
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import javax.inject.Inject

class GetKeyUseCase @Inject constructor(
    private val queryBus: QueryBus,
    private val observeUserUseCase: ObserveUserUseCase
) {

    suspend operator fun invoke(): Key? = observeUserUseCase()
        .first()
        ?.let { user -> FindAllKeysQuery(userId = user.id) }
        ?.let { query -> queryBus.ask<List<Key>>(query) }
        ?.first()
        ?.firstOrNull()

}
