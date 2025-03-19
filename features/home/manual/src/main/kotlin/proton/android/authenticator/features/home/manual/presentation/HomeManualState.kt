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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType

internal class HomeManualState private constructor(
    internal val event: HomeManualEvent,
    internal val formModel: HomeManualFormModel
) {

    internal companion object {

        private const val DEFAULT_DIGITS = 6

        private const val DEFAULT_TIME_INTERVAL = 30

        private val DEFAULT_ALGORITHM = EntryAlgorithm.SHA1

        private val DEFAULT_TYPE = EntryType.TOTP

        @Composable
        internal fun create(
            eventFlow: Flow<HomeManualEvent>,
            entryFlow: Flow<Entry?>,
            titleFlow: Flow<String?>,
            secretFlow: Flow<String?>,
            issuerFlow: Flow<String?>,
            digitsFlow: Flow<Int?>,
            timeIntervalFlow: Flow<Int?>,
            algorithmFlow: Flow<EntryAlgorithm?>,
            typeFlow: Flow<EntryType?>
        ): HomeManualState {
            val event by eventFlow.collectAsState(initial = HomeManualEvent.Idle)
            val entry by entryFlow.collectAsState(initial = null)
            val title by titleFlow.collectAsState(initial = null)
            val secret by secretFlow.collectAsState(initial = null)
            val issuer by issuerFlow.collectAsState(initial = null)
            val digits by digitsFlow.collectAsState(initial = null)
            val timeInterval by timeIntervalFlow.collectAsState(initial = null)
            val algorithm by algorithmFlow.collectAsState(initial = null)
            val type by typeFlow.collectAsState(initial = EntryType.TOTP)

            val initialTitle = entry?.name.orEmpty()
            val initialSecret = entry?.secret.orEmpty()
            val initialIssuer = entry?.issuer.orEmpty()

            return HomeManualState(
                event = event,
                formModel = HomeManualFormModel(
                    initialTitle = initialTitle,
                    title = title ?: initialTitle,
                    initialSecret = initialSecret,
                    secret = secret ?: initialSecret,
                    initialIssuer = initialIssuer,
                    issuer = issuer ?: initialIssuer,
                    digits = digits ?: entry?.digits ?: DEFAULT_DIGITS,
                    timeInterval = timeInterval ?: entry?.period ?: DEFAULT_TIME_INTERVAL,
                    algorithm = algorithm ?: entry?.algorithm ?: DEFAULT_ALGORITHM,
                    type = type ?: entry?.type ?: DEFAULT_TYPE
                )
            )
        }

    }

}
