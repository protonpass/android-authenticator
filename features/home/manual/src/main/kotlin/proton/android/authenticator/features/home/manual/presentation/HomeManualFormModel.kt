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

import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.business.entries.domain.EntryType

internal data class HomeManualFormModel(
    internal val initialTitle: String,
    internal val title: String,
    internal val initialSecret: String,
    internal val secret: String,
    internal val initialIssuer: String,
    internal val issuer: String,
    internal val digits: Int,
    internal val timeInterval: Int,
    internal val algorithm: EntryAlgorithm,
    internal val type: EntryType
) {

    internal val digitsOptions: List<Int> = setOf(5, 6, 7, 8)
        .let { digitsOptions ->
            if (digitsOptions.contains(digits)) digitsOptions.toList()
            else digitsOptions.plus(digits).sorted()
        }

    internal val timeIntervalOptions: List<Int> = setOf(30, 60)
        .let { timeIntervalOptions ->
            if (timeIntervalOptions.contains(timeInterval)) timeIntervalOptions.toList()
            else timeIntervalOptions.plus(timeInterval).sorted()
        }

    internal val algorithmOptions: List<String> = EntryAlgorithm.entries
        .map { algorithm -> algorithm.name }

    internal val selectedAlgorithmIndex: Int = algorithm.value

    internal val typeOptions: List<String> = EntryType.entries
        .map { type -> type.name }

    internal val selectedTypeIndex: Int = type.value

    internal val isValid: Boolean = secret.isNotBlank()

}
