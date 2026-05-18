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

package proton.android.authenticator.features.imports.onboarding.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import proton.android.authenticator.business.entries.domain.EntryImportType
import proton.android.authenticator.features.imports.onboarding.presentation.ImportOnboardingEvent
import proton.android.authenticator.features.imports.onboarding.presentation.ImportOnboardingState
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePreviewProvider

@Composable
internal fun ImportsOnboardingContent(
    state: ImportOnboardingState,
    onHelpClick: (String) -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    if (isSupported) {
        ImportsOnboardingSupported(
            modifier = modifier,
            providerIcon = providerIcon,
            providerNameText = providerNameText,
            providerStepsResId = providerStepsResId,
            helpUrl = helpUrl,
            onHelpClick = onHelpClick
        )
    } else {
        ImportsOnboardingUnsupported(
            modifier = modifier,
            providerIcon = providerIcon,
            providerNameText = providerNameText,
            providerStepsResId = providerStepsResId
        )
    }
}

@Preview
@Composable
fun ImportsOnboardingContentPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    Theme(isDarkTheme = isDark) {
        Surface {
            ImportsOnboardingContent(
                state = ImportOnboardingState(
                    event = ImportOnboardingEvent.Idle,
                    importType = EntryImportType.Google
                ),
                onHelpClick = {}
            )
        }
    }
}
