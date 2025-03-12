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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import proton.android.authenticator.business.entries.application.shared.responses.EntryQueryResponse
import proton.android.authenticator.business.entrycodes.application.shared.responses.EntryCodeQueryResponse

internal class HomeMasterState private constructor(
    private val entryModelsMap: MutableState<MutableMap<Int, HomeMasterEntryModel>>
) {

    internal val entryModels: List<HomeMasterEntryModel>
        get() = entryModelsMap
            .value
            .values
            .toList()

    internal val hasEntryModels: Boolean = entryModels.isNotEmpty()

    internal companion object {

        @Composable
        internal fun create(
            entries: List<EntryQueryResponse>,
            entryCodes: List<EntryCodeQueryResponse>
        ): HomeMasterState {
            return entries
                .zip(entryCodes) { entry, entryCode ->
                    HomeMasterEntryModel(
                        id = entry.id,
                        name = entry.name,
                        currentCode = entryCode.currentCode,
                        nextCode = entryCode.nextCode,
                        remainingSeconds = entryCode.remainingSeconds,
                        totalSeconds = entry.period,
                        isRevealed = false
                    )
                }
                .associateBy { entryModel -> entryModel.id }
                .toMutableMap()
                .let(::mutableStateOf)
                .let(::HomeMasterState)
        }

    }

}
