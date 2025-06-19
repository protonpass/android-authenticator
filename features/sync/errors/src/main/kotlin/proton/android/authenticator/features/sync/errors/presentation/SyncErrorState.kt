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

package proton.android.authenticator.features.sync.errors.presentation

import androidx.compose.runtime.Stable
import proton.android.authenticator.features.sync.shared.presentation.SyncErrorType
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.features.sync.errors.R

@Stable
internal data class SyncErrorState(private val errorType: SyncErrorType) {

    internal val errorText: UiText = when (errorType) {
        SyncErrorType.DisableSync -> R.string.sync_error_text_disable
        SyncErrorType.EnableSync -> R.string.sync_error_text_enable
    }.let(UiText::Resource)

}
