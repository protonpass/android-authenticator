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

package proton.android.authenticator.features.backups.errors.presentation

import androidx.compose.runtime.Immutable
import proton.android.authenticator.business.backups.application.generate.GenerateBackupReason
import proton.android.authenticator.features.backups.errors.R
import proton.android.authenticator.shared.ui.domain.models.UiText

@Immutable
internal data class BackupsErrorsState(private val errorReason: GenerateBackupReason) {

    internal val messageTexts: List<UiText> = buildList {
        when (errorReason) {
            GenerateBackupReason.CannotGenerate,
            GenerateBackupReason.NoEntries,
            GenerateBackupReason.NotEnabled -> R.string.backup_error_dialog_message_error_unknown

            GenerateBackupReason.MissingFileName,
            GenerateBackupReason.FileCreationFailed -> R.string.backup_error_dialog_message_error_folder
        }
            .let(UiText::Resource)
            .also(::add)

        UiText.Resource(id = R.string.backup_error_dialog_message_error_suggestion)
            .also(::add)
    }

}
