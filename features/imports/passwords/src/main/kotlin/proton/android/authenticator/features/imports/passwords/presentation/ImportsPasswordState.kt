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

package proton.android.authenticator.features.imports.passwords.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.features.imports.passwords.R
import proton.android.authenticator.shared.ui.domain.models.UiText

internal class ImportsPasswordState private constructor(
    internal val password: String,
    internal val isPasswordError: Boolean,
    internal val isPasswordVisible: Boolean,
    internal val event: ImportsPasswordEvent
) {

    internal val passwordErrorText: UiText? = if (isPasswordError) {
        UiText.Resource(id = R.string.imports_password_incorrect_password)
    } else {
        null
    }

    internal val isValidPassword: Boolean = password.isNotBlank() && !isPasswordError

    internal companion object {

        @Composable
        internal fun create(
            password: String?,
            isPasswordErrorFlow: Flow<Boolean>,
            isPasswordVisibleFlow: Flow<Boolean>,
            eventFlow: Flow<ImportsPasswordEvent>
        ): ImportsPasswordState {
            val isPasswordError by isPasswordErrorFlow.collectAsState(initial = false)
            val isPasswordVisible by isPasswordVisibleFlow.collectAsState(initial = false)
            val event by eventFlow.collectAsState(initial = ImportsPasswordEvent.Idle)

            return ImportsPasswordState(
                password = password.orEmpty(),
                isPasswordError = isPasswordError,
                isPasswordVisible = isPasswordVisible,
                event = event
            )
        }
    }

}
