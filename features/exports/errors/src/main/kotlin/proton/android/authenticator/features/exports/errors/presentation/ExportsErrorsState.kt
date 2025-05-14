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

package proton.android.authenticator.features.exports.errors.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import proton.android.authenticator.business.entries.application.exportall.ExportEntriesReason
import proton.android.authenticator.features.exports.errors.R
import proton.android.authenticator.shared.ui.domain.models.UiText

@Immutable
internal class ExportsErrorsState private constructor(private val errorReason: ExportEntriesReason) {

    internal val errorText: UiText = when (errorReason) {
        ExportEntriesReason.InvalidEntries -> R.string.exports_error_dialog_message_error
        ExportEntriesReason.InvalidPath -> R.string.exports_error_dialog_message_error_invalid_path
    }.let(UiText::Resource)

    internal companion object {

        @Composable
        internal fun create(errorReason: ExportEntriesReason): ExportsErrorsState = ExportsErrorsState(
            errorReason = errorReason
        )

    }

}
