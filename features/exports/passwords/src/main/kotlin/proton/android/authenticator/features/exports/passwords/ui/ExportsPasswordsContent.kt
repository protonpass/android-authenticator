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

package proton.android.authenticator.features.exports.passwords.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import proton.android.authenticator.features.exports.passwords.R
import proton.android.authenticator.features.exports.passwords.presentation.ExportsPasswordsState
import proton.android.authenticator.shared.ui.domain.components.buttons.VerticalActionsButtons
import proton.android.authenticator.shared.ui.domain.components.textfields.StandaloneSecureTextField
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun ExportsPasswordsContent(
    state: ExportsPasswordsState,
    onPasswordChange: (String) -> Unit,
    onVisibilityChange: (Boolean) -> Unit,
    onExportWithPassword: () -> Unit,
    onExportWithoutPassword: () -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.exports_password_title),
                textAlign = TextAlign.Center,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.subtitle
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ThemePadding.Medium),
                text = stringResource(id = R.string.exports_password_subtitle),
                textAlign = TextAlign.Center,
                color = Theme.colorScheme.textWeak,
                style = Theme.typography.bodyRegular
            )
        }

        StandaloneSecureTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = password,
            onValueChange = onPasswordChange,
            isVisible = isPasswordVisible,
            onVisibilityChange = onVisibilityChange
        )

        VerticalActionsButtons(
            modifier = Modifier.fillMaxWidth(),
            primaryActionText = stringResource(id = R.string.exports_password_action_primary),
            onPrimaryActionClick = onExportWithPassword,
            isPrimaryActionEnabled = isValidPassword,
            secondaryActionText = stringResource(id = R.string.exports_password_action_secondary),
            onSecondaryActionClick = onExportWithoutPassword
        )
    }
}
