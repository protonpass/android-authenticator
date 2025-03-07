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

package proton.android.authenticator.shared.common.infrastructure.queries

import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.shared.common.domain.infrastructure.queries.Query
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryResponse
import javax.inject.Inject

internal class InMemoryQueryBus @Inject constructor(
    private val queryHandlers: Map<Class<out Query>, @JvmSuppressWildcards QueryHandler<*, *>>
) : QueryBus {

    @Suppress("UNCHECKED_CAST")
    override fun <R : QueryResponse> ask(query: Query): Flow<R> = queryHandlers[query::class.java]
        ?.let { queryHandler -> (queryHandler as QueryHandler<Query, R>).handle(query) }
        ?: throw IllegalArgumentException("No query handler found for query: ${query::class.simpleName}")

}
