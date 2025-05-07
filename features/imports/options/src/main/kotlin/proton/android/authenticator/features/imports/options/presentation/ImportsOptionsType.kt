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
import proton.android.authenticator.features.imports.options.R

internal enum class ImportsOptionsType(@StringRes internal val id: Int) {
    Google(id = R.string.imports_options_option_google),
    TwoFas(id = R.string.imports_options_option_2fas),
    Aegis(id = R.string.imports_options_option_aegis),
    Bitwarden(id = R.string.imports_options_option_bitwarden),
    Ente(id = R.string.imports_options_option_ente),
    LastPass(id = R.string.imports_options_option_last_pass),
    Proton(id = R.string.imports_options_option_proton)
}
