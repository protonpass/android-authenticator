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

package proton.android.authenticator.features.home.master.usecases

import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entrycodes.application.search.SearchEntryCodesQuery
import proton.android.authenticator.business.entrycodes.domain.EntryCode
import proton.android.authenticator.features.shared.entries.presentation.EntryModel
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus
import javax.inject.Inject

internal class ObserveEntryCodesUseCase @Inject constructor(private val queryBus: QueryBus) {

    internal operator fun invoke(entryModels: List<EntryModel>): Flow<List<EntryCode>> = entryModels
        .map(EntryModel::uri)
        .let(::SearchEntryCodesQuery)
        .let { query -> queryBus.ask(query) }

}
