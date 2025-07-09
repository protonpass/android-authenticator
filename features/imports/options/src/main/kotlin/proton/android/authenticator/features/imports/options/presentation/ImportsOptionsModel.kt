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
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText

internal data class ImportsOptionsModel(
    internal val type: EntryImportType,
    @StringRes private val nameResId: Int
) {

    internal val id: String = type.name

    internal val nameText: UiText = UiText.Resource(id = nameResId)

    internal val icon: UiIcon = when (type) {
        EntryImportType.Aegis -> R.drawable.ic_authenticator_aegis
        EntryImportType.Authy -> R.drawable.ic_authenticator_authy
        EntryImportType.Bitwarden -> R.drawable.ic_authenticator_bitwarden
        EntryImportType.Ente -> R.drawable.ic_authenticator_ente
        EntryImportType.Google -> R.drawable.ic_authenticator_google
        EntryImportType.LastPass -> R.drawable.ic_authenticator_lastpass
        EntryImportType.Microsoft -> R.drawable.ic_authenticator_microsoft
        EntryImportType.ProtonAuthenticator -> R.drawable.ic_authenticator_proton_authenticator
        EntryImportType.ProtonPass -> R.drawable.ic_authenticator_proton_pass
        EntryImportType.TwoFas -> R.drawable.ic_authenticator_2fas
    }.let(UiIcon::Resource)

}
