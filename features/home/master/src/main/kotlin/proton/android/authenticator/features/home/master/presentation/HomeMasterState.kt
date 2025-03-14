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

package proton.android.authenticator.features.home.master.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entrycodes.domain.EntryCode

internal class HomeMasterState private constructor(
    private val entryModelsMap: Map<Int, HomeMasterEntryModel>
) {

    internal val entryModels: List<HomeMasterEntryModel>
        get() = entryModelsMap.values
            .toList()

    internal val hasEntryModels: Boolean = entryModels.isNotEmpty()

    internal companion object {

        @Composable
        internal fun create(
            entriesFlow: Flow<List<Entry>>,
            entryCodesFlow: Flow<List<EntryCode>>,
            entryCodesRemainingTimesFlow: Flow<Map<Int, Int>>,
            entrySearchQueryFlow: Flow<String>
        ): HomeMasterState {
            val entries by entriesFlow.collectAsState(emptyList())
            val entryCodes by entryCodesFlow.collectAsState(emptyList())
            val entryCodesRemainingTimes by entryCodesRemainingTimesFlow.collectAsState(emptyMap())
            val entrySearchQuery by entrySearchQueryFlow.collectAsState("")

            return entries.zip(entryCodes) { entry, entryCode ->
                HomeMasterEntryModel(
                    id = entry.id,
                    name = entry.name,
                    issuer = entry.issuer,
                    currentCode = entryCode.currentCode,
                    nextCode = entryCode.nextCode,
                    remainingSeconds = entryCodesRemainingTimes.getOrDefault(entry.period, 0),
                    totalSeconds = entry.period
                )
            }
                .filter { entryModel ->
                    if (entrySearchQuery.isEmpty()) true
                    else entryModel.name.contains(entrySearchQuery, ignoreCase = true)
                }
                .associateBy { entryModel -> entryModel.id }
                .let(::HomeMasterState)
        }

    }

}
