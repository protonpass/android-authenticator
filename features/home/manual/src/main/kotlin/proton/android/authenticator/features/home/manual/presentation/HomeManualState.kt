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
import kotlinx.coroutines.flow.filterNotNull
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType

internal class HomeManualState private constructor(
    internal val formModel: HomeManualFormModel
) {

    internal companion object {

        @Composable
        internal fun create(
            entryFlow: Flow<Entry?>,
            titleFlow: Flow<String?>,
            secretFlow: Flow<String?>,
            issuerFlow: Flow<String?>,
            digitsFlow: Flow<Int?>,
            timeIntervalFlow: Flow<Int?>,
            algorithmFlow: Flow<EntryAlgorithm?>,
            typeFlow: Flow<EntryType?>
        ): HomeManualState {
            val entry by entryFlow.collectAsState(initial = null)
            val initialTitle = entry?.name.orEmpty()
            val initialSecret = entry?.secret.orEmpty()
            val initialIssuer = entry?.issuer.orEmpty()

            val title by titleFlow
                .filterNotNull()
                .collectAsState(initial = initialTitle)

            val secret by secretFlow
                .filterNotNull()
                .collectAsState(initial = initialSecret)

            val issuer by issuerFlow
                .filterNotNull()
                .collectAsState(initial = initialIssuer)

            val digits by digitsFlow
                .filterNotNull()
                .collectAsState(initial = 6)

            val timeInterval by timeIntervalFlow
                .filterNotNull()
                .collectAsState(initial = 30)

            val algorithm by algorithmFlow
                .filterNotNull()
                .collectAsState(initial = EntryAlgorithm.SHA1)

            val type by typeFlow
                .filterNotNull()
                .collectAsState(initial = EntryType.TOTP)

            return HomeManualState(
                formModel = HomeManualFormModel(
                    initialTitle = initialTitle,
                    title = title,
                    initialSecret = initialTitle,
                    secret = secret,
                    initialIssuer = initialIssuer,
                    issuer = issuer,
                    digits = digits,
                    timeInterval = timeInterval,
                    algorithm = algorithm,
                    type = type
                )
            )
        }

    }

}
