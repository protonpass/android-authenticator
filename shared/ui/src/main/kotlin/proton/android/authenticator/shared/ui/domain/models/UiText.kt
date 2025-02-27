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

package proton.android.authenticator.shared.ui.domain.models

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.shared.ui.domain.models.UiText.Mode

sealed class UiText {

    internal enum class Mode {
        Default,
        Lowercase,
        Uppercase
    }

    internal abstract var mode: Mode

    @Composable
    internal abstract fun asString(): String

    @Stable
    class Dynamic(
        private val value: String
    ) : UiText() {

        override var mode: Mode = Mode.Default

        @Composable
        override fun asString(): String = value.applyMode(mode)

    }

    @Stable
    class Resource(
        @StringRes private val resId: Int,
        private vararg val args: Any = emptyArray()
    ) : UiText() {


        override var mode: Mode = Mode.Default

        @Composable
        override fun asString(): String = stringResource(id = resId, formatArgs = args)
            .applyMode(mode)

    }

}

private fun String.applyMode(mode: Mode): String = when (mode) {
    Mode.Default -> this
    Mode.Lowercase -> lowercase()
    Mode.Uppercase -> uppercase()
}
