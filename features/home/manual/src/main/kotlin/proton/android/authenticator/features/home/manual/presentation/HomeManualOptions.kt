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

import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiSelectorOption
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface HomeManualOptions : UiSelectorOption<Int> {

    data class Digits(
        override val selectedType: Int,
        private val digits: Int
    ) : HomeManualOptions {

        override val isSelected: Boolean = selectedType == digits

        override val text: UiText = UiText.Dynamic(value = digits.toString())

        override val value: Int = digits

    }

    data class TimeInterval(
        override val selectedType: Int,
        private val timeInterval: Int
    ) : HomeManualOptions {

        override val isSelected: Boolean = selectedType == timeInterval

        override val text: UiText = UiText.PluralResource(
            id = R.plurals.unit_seconds,
            count = timeInterval,
            args = arrayOf(timeInterval)
        )

        override val value: Int = timeInterval

    }

}
