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

import androidx.annotation.StringRes
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.ui.domain.models.UiText

internal data class ImportsOptionsModel(
    internal val type: EntryImportType,
    @StringRes private val nameResId: Int
) {

    internal val id: String = type.name

    internal val nameText: UiText = UiText.Resource(id = nameResId)

    internal val mimeTypes: List<String> = type.mimeTypes.map(MimeType::value)

}
