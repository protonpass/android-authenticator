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

import androidx.compose.runtime.Stable
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.shared.ui.R

@Stable
internal data object ImportsOptionsState {

    internal val optionModels: List<ImportsOptionsModel> = listOf(
        ImportsOptionsModel(
            type = EntryImportType.Google,
            nameResId = R.string.authenticator_google
        ),
        ImportsOptionsModel(
            type = EntryImportType.Authy,
            nameResId = R.string.authenticator_authy
        ),
        ImportsOptionsModel(
            type = EntryImportType.TwoFas,
            nameResId = R.string.authenticator_2fas
        ),
        ImportsOptionsModel(
            type = EntryImportType.Aegis,
            nameResId = R.string.authenticator_aegis
        ),
        ImportsOptionsModel(
            type = EntryImportType.Bitwarden,
            nameResId = R.string.authenticator_bitwarden
        ),
        ImportsOptionsModel(
            type = EntryImportType.Ente,
            nameResId = R.string.authenticator_ente
        ),
        ImportsOptionsModel(
            type = EntryImportType.LastPass,
            nameResId = R.string.authenticator_last_pass
        ),
        ImportsOptionsModel(
            type = EntryImportType.Microsoft,
            nameResId = R.string.authenticator_microsoft
        ),
        ImportsOptionsModel(
            type = EntryImportType.Proton,
            nameResId = R.string.authenticator_proton
        )
    )

}
