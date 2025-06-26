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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.imports.onboarding.R
import proton.android.authenticator.shared.ui.domain.components.icons.ProviderIcon
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundFireball
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun ImportsOnboardingUnsupported(
    providerIcon: UiIcon,
    providerNameText: UiText,
    providerStepsResId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ThemePadding.Large)
                .offset(y = -ThemeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(size = 225.dp)
                        .backgroundFireball(size = 225.dp)
                )

                ProviderIcon(
                    icon = providerIcon,
                    size = ThemeSpacing.ExtraLarge.plus(ThemeSpacing.Large),
                    borderRadius = ThemeSpacing.MediumLarge,
                    alpha = 0.8f
                )
            }

            Spacer(modifier = Modifier.padding(vertical = ThemeSpacing.Small))

            Text(
                text = stringResource(id = R.string.imports_onboarding_title_unsupported),
                textAlign = TextAlign.Center,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.subtitle
            )

            stringArrayResource(id = providerStepsResId).forEach { step ->
                Text(
                    text = String.format(step, providerNameText.asString()),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.bodyRegular
                )
            }
        }
    }
}
