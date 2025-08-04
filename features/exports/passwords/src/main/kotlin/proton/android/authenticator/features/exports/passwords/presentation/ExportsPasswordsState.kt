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

package proton.android.authenticator.features.exports.passwords.presentation

import androidx.compose.runtime.Stable
import proton.android.authenticator.shared.common.domain.models.MimeType

@Stable
internal data class ExportsPasswordsState(
    internal val password: String,
    internal val isPasswordVisible: Boolean,
    internal val event: ExportsPasswordsEvent
) {

    internal val exportFileMimeType: String = MimeType.Json.value

    internal val isValidPassword: Boolean = password.isNotBlank()

    internal companion object {

        internal const val FILE_NAME = "proton_authenticator_backup.json"

        internal val Initial: ExportsPasswordsState = ExportsPasswordsState(
            password = "",
            isPasswordVisible = false,
            event = ExportsPasswordsEvent.Idle
        )

    }

}
