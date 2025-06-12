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

import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.options.R

internal data class ImportsOptionsState(
    internal val selectedOptionModel: ImportsOptionsModel?,
    internal val event: ImportsOptionsEvent,
    internal val optionModels: List<ImportsOptionsModel> = DefaultOptionModels
) {

    internal companion object {

        private val DefaultOptionModels: List<ImportsOptionsModel> = listOf(
            ImportsOptionsModel(
                type = EntryImportType.Google,
                nameResId = R.string.imports_options_option_google
            ),
            ImportsOptionsModel(
                type = EntryImportType.TwoFas,
                nameResId = R.string.imports_options_option_2fas
            ),
            ImportsOptionsModel(
                type = EntryImportType.Aegis,
                nameResId = R.string.imports_options_option_aegis
            ),
            ImportsOptionsModel(
                type = EntryImportType.Bitwarden,
                nameResId = R.string.imports_options_option_bitwarden
            ),
            ImportsOptionsModel(
                type = EntryImportType.Ente,
                nameResId = R.string.imports_options_option_ente
            ),
            ImportsOptionsModel(
                type = EntryImportType.LastPass,
                nameResId = R.string.imports_options_option_last_pass
            ),
            ImportsOptionsModel(
                type = EntryImportType.Proton,
                nameResId = R.string.imports_options_option_proton
            )
        )

    }

}
