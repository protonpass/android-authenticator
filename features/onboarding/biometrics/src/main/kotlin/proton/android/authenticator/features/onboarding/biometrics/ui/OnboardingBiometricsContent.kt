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

package proton.android.authenticator.features.onboarding.biometrics.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import proton.android.authenticator.features.onboarding.biometrics.R
import proton.android.authenticator.shared.ui.domain.components.bars.VerticalActionsBottomBar
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun OnboardingBiometricsContent(onEnableBiometricsClick: () -> Unit, onSkipClick: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .backgroundScreenGradient(),
        containerColor = Color.Transparent,
        bottomBar = {
            VerticalActionsBottomBar(
                primaryActionText = stringResource(id = R.string.onboarding_biometrics_action_enable_biometrics),
                onPrimaryActionClick = onEnableBiometricsClick,
                secondaryActionText = stringResource(id = uiR.string.action_skip),
                onSecondaryActionClick = onSkipClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(horizontal = ThemePadding.Large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
            ) {
                Image(
                    painter = painterResource(id = uiR.drawable.ic_placeholder_biometrics),
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = ThemeSpacing.Large)
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.onboarding_biometrics_title),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.subtitle
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ThemePadding.Medium),
                    text = stringResource(id = R.string.onboarding_biometrics_subtitle),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.bodyRegular
                )
            }
        }
    }
}
