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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import proton.android.authenticator.features.imports.onboarding.R
import proton.android.authenticator.shared.common.domain.constants.CharacterConstants
import proton.android.authenticator.shared.ui.domain.components.buttons.LinkButton
import proton.android.authenticator.shared.ui.domain.components.icons.ProviderIcon
import proton.android.authenticator.shared.ui.domain.components.texts.DelimiterStyledText
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun ImportsOnboardingSupported(
    providerIcon: UiIcon,
    providerNameText: UiText,
    providerStepsResId: Int,
    helpUrl: String,
    onHelpClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium)
    ) {
        Row(
            modifier = Modifier.padding(vertical = ThemePadding.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
        ) {
            ProviderIcon(
                icon = providerIcon,
                size = ThemeSpacing.ExtraLarge
            )

            Image(
                painter = painterResource(id = uiR.drawable.ic_arrow_right_gradient),
                contentDescription = null
            )

            ProviderIcon(
                icon = UiIcon.Resource(id = uiR.drawable.ic_authenticator_proton_authenticator),
                size = ThemeSpacing.ExtraLarge
            )
        }

        Text(
            text = stringResource(
                id = R.string.imports_onboarding_title_supported,
                providerNameText.asString()
            ),
            color = Theme.colorScheme.textNorm,
            style = Theme.typography.subtitle
        )

        stringArrayResource(id = providerStepsResId).forEach { step ->
            DelimiterStyledText(
                text = step,
                delimiter = CharacterConstants.DOUBLE_QUOTES,
                textColor = Theme.colorScheme.textWeak,
                textStyle = Theme.typography.bodyRegular,
                delimitedTextStyle = Theme.typography.bodyBold
            )
        }

        LinkButton(
            modifier = Modifier.offset(x = -ThemePadding.MediumSmall),
            linkText = UiText.Resource(id = R.string.imports_onboarding_help_link),
            onLinkClick = { onHelpClick(helpUrl) }
        )
    }
}
