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

package proton.android.authenticator.business.keys.application.findall

import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.keys.domain.Key
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Inject

internal class FindAllKeysQueryHandler @Inject constructor(
    private val finder: AllKeysFinder
) : QueryHandler<FindAllKeysQuery, List<Key>> {

    override fun handle(query: FindAllKeysQuery): Flow<List<Key>> = finder
        .findAll(userId = query.userId)

}
