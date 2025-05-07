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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.options.R

internal class ImportsOptionsState private constructor(
    internal val optionModels: List<ImportsOptionsModel>,
    internal val selectedOptionModel: ImportsOptionsModel?,
    internal val event: ImportsOptionsEvent
) {

    internal companion object {

        private val optionModels = listOf(
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

        @Composable
        internal fun create(
            selectedOptionFlow: Flow<ImportsOptionsModel?>,
            eventFlow: Flow<ImportsOptionsEvent>
        ): ImportsOptionsState {
            val selectedOption by selectedOptionFlow.collectAsState(initial = null)
            val event by eventFlow.collectAsState(ImportsOptionsEvent.Idle)

            return ImportsOptionsState(
                optionModels = optionModels,
                selectedOptionModel = selectedOption,
                event = event
            )
        }

    }

}
