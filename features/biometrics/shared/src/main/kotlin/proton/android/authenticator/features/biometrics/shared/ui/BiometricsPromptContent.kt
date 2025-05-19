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

package proton.android.authenticator.features.biometrics.shared.ui

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import proton.android.authenticator.features.biometrics.shared.presentation.BiometricsErrorType
import proton.android.authenticator.features.biometrics.shared.presentation.BiometricsSharedState
import proton.android.authenticator.shared.ui.domain.models.UiText

@Composable
internal fun BiometricsPromptContent(
    state: BiometricsSharedState.Ready,
    titleText: UiText,
    subtitleText: UiText,
    onError: (BiometricsErrorType) -> Unit,
    onSuccess: () -> Unit
) {
    var hasBeenShown by remember {
        mutableStateOf(value = false)
    }

    if (hasBeenShown) {
        return
    }

    val context = LocalContext.current

    if (context !is FragmentActivity) {
        return
    }

    val executor = remember {
        ContextCompat.getMainExecutor(context)
    }

    val biometricPrompt = remember {
        BiometricPrompt(
            context, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    onError(BiometricsErrorType.from(errorCode))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    onSuccess()
                }

            }
        )
    }

    BiometricPrompt.PromptInfo.Builder()
        .setTitle(titleText.asString())
        .setSubtitle(subtitleText.asString())
        .setAllowedAuthenticators(state.allowedAuthenticators)
        .build()
        .also(biometricPrompt::authenticate)
        .also { hasBeenShown = true }
}
