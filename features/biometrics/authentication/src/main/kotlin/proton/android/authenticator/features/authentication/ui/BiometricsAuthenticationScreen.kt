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

package proton.android.authenticator.features.authentication.ui

import androidx.compose.runtime.Composable
import proton.android.authenticator.features.biometrics.authentication.R
import proton.android.authenticator.features.biometrics.shared.ui.BiometricsPromptScreen
import proton.android.authenticator.shared.ui.domain.models.UiText

@Composable
fun BiometricsAuthenticationScreen(onCancelled: () -> Unit) {
    BiometricsPromptScreen(
        titleText = UiText.Resource(id = R.string.biometrics_authentication_title),
        subtitleText = UiText.Resource(id = R.string.biometrics_authentication_subtitle),
        onError = {
            println("JIBIRI: auth onError")
        },
        onSuccess = {
            println("JIBIRI: auth onSuccess")
        }
    )
}
