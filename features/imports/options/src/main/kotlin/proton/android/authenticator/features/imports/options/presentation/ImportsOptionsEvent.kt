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

package proton.android.authenticator.features.imports.options.presentation

internal sealed interface ImportsOptionsEvent {

    data object Idle : ImportsOptionsEvent

    data class OnChooseFile(
        internal val isMultiSelectionAllowed: Boolean,
        internal val mimeTypes: List<String>
    ) : ImportsOptionsEvent

    data class OnFileImported(internal val importedEntriesCount: Int) : ImportsOptionsEvent

    data class OnFileImportFailed(internal val reason: Int) : ImportsOptionsEvent

    data class OnFilePasswordRequired(
        internal val uri: String,
        internal val importType: Int
    ) : ImportsOptionsEvent

}
