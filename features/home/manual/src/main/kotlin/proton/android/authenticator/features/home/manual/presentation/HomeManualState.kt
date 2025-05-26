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

package proton.android.authenticator.features.home.manual.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType

@Immutable
internal sealed interface HomeManualState {

    val event: HomeManualEvent

    @Immutable
    data object Loading : HomeManualState {

        override val event: HomeManualEvent = HomeManualEvent.Idle

    }

    @Immutable
    data class Editing(
        override val event: HomeManualEvent,
        internal val formModel: HomeManualFormModel
    ) : HomeManualState

    @Immutable
    data class Creating(
        override val event: HomeManualEvent,
        internal val formModel: HomeManualFormModel
    ) : HomeManualState


    companion object {

        private const val DEFAULT_DIGITS = 6

        private const val DEFAULT_TIME_INTERVAL = 30

        private val DEFAULT_ALGORITHM = EntryAlgorithm.SHA1

        private val DEFAULT_TYPE = EntryType.TOTP

        @Composable
        internal fun create(
            entryId: String?,
            entryFlow: Flow<Entry?>,
            title: String?,
            secret: String?,
            isValidSecretFlow: Flow<Boolean>,
            issuer: String?,
            digitsFlow: Flow<Int?>,
            timeIntervalFlow: Flow<Int?>,
            algorithmFlow: Flow<EntryAlgorithm?>,
            typeFlow: Flow<EntryType?>,
            showAdvanceOptionsFlow: Flow<Boolean?>,
            eventFlow: Flow<HomeManualEvent>
        ): HomeManualState {
            val entry by entryFlow.collectAsState(initial = null)
            val digits by digitsFlow.collectAsState(initial = null)
            val timeInterval by timeIntervalFlow.collectAsState(initial = null)
            val algorithm by algorithmFlow.collectAsState(initial = null)
            val type by typeFlow.collectAsState(initial = null)
            val showAdvanceOptions by showAdvanceOptionsFlow.collectAsState(initial = null)
            val event by eventFlow.collectAsState(initial = HomeManualEvent.Idle)
            val isValidSecret by isValidSecretFlow.collectAsState(initial = false)

            if (entryId == null) {
                return Creating(
                    formModel = HomeManualFormModel(
                        title = title.orEmpty(),
                        secret = secret.orEmpty(),
                        issuer = issuer.orEmpty(),
                        digits = digits ?: DEFAULT_DIGITS,
                        timeInterval = timeInterval ?: DEFAULT_TIME_INTERVAL,
                        algorithm = algorithm ?: DEFAULT_ALGORITHM,
                        type = type ?: DEFAULT_TYPE,
                        position = 0.0,
                        showAdvanceOptions = showAdvanceOptions == true,
                        isValidSecret = isValidSecret
                    ),
                    event = event
                )
            }

            return entry?.let { currentEntry ->
                Editing(
                    formModel = HomeManualFormModel(
                        title = title ?: currentEntry.name,
                        secret = secret ?: currentEntry.secret,
                        issuer = issuer ?: currentEntry.issuer,
                        digits = digits ?: currentEntry.digits,
                        timeInterval = timeInterval ?: currentEntry.period,
                        algorithm = algorithm ?: currentEntry.algorithm,
                        type = type ?: currentEntry.type,
                        position = currentEntry.position,
                        showAdvanceOptions = showAdvanceOptions == true,
                        isValidSecret = isValidSecret
                    ),
                    event = event
                )
            } ?: Loading
        }
    }

}
