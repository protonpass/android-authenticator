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
    internal val title: String,
    internal val secret: String,
    internal val issuer: String,
    internal val digits: Int,
    internal val timeInterval: Int,
    internal val algorithm: EntryAlgorithm,
    internal val type: EntryType,
    internal val showAdvanceOptions: Boolean,
    internal val position: Double,
    internal val mode: HomeManualMode,
    private val isValidSecret: Boolean,
    private val isValidTitle: Boolean
) {

    internal val isSecretError: Boolean = !isValidSecret
    internal val isTitleError: Boolean = !isValidTitle

    internal val digitsOptions: List<HomeManualOptions.Digits> = DEFAULT_DIGITS
        .let { digitsOptions ->
            if (digitsOptions.contains(digits)) digitsOptions.toList()
            else digitsOptions.plus(digits).sorted()
        }
        .map { availableDigits ->
            HomeManualOptions.Digits(
                selectedType = digits,
                digits = availableDigits
            )
        }

    internal val timeIntervalOptions: List<HomeManualOptions.TimeInterval> = DEFAULT_TIME_INTERVALS
        .let { timeIntervalOptions ->
            if (timeIntervalOptions.contains(timeInterval)) timeIntervalOptions.toList()
            else timeIntervalOptions.plus(timeInterval).sorted()
        }
        .map { availableTimeInterval ->
            HomeManualOptions.TimeInterval(
                selectedType = timeInterval,
                timeInterval = availableTimeInterval
            )
        }

    internal val algorithmOptions: List<String> = EntryAlgorithm.entries
        .map { algorithm -> algorithm.name }

    internal val selectedAlgorithmIndex: Int = algorithm.value

    internal val typeOptions: List<String> = EntryType.entries
        .map { type -> type.name }

    internal val selectedTypeIndex: Int = type.value

    internal val isValid: Boolean = secret.isNotBlank() && isValidSecret && isValidTitle

    private companion object {

        private val DEFAULT_DIGITS = setOf(6, 7, 8)

        private val DEFAULT_TIME_INTERVALS = setOf(30, 60)

    }

}
