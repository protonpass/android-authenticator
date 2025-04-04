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

package proton.android.authenticator.features.onboarding.master.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.onboarding.master.R
import proton.android.authenticator.shared.ui.domain.components.buttons.PrimaryActionButton
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundOnboarding
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun OnboardingMasterContent(onGetStartedClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 380.dp)
                    .backgroundOnboarding()
            )

            Column(
                modifier = Modifier.padding(
                    start = ThemePadding.Large,
                    end = ThemePadding.Large,
                    bottom = ThemePadding.Large
                ),
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.onboarding_master_title),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.subtitle
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ThemePadding.Large),
                    text = stringResource(id = R.string.onboarding_master_subtitle),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.bodyRegular
                )
            }

            PrimaryActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ThemePadding.Large),
                text = stringResource(id = R.string.onboarding_master_action),
                onClick = onGetStartedClick
            )
        }
    }
}
