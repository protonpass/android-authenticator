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

package proton.android.authenticator.business.entries.application.sortall

import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.entries.domain.EntriesRepository
import javax.inject.Inject

internal class EntriesSorter @Inject constructor(private val repository: EntriesRepository) {

    internal suspend fun sort(sortingMap: Map<String, Int>) {
        repository.findAll()
            .first()
            .mapNotNull { entry ->
                sortingMap[entry.id]?.let { newPosition -> entry.copy(position = newPosition) }
            }
            .also { sortedEntries ->
                repository.saveAll(entries = sortedEntries)
            }
    }

}
