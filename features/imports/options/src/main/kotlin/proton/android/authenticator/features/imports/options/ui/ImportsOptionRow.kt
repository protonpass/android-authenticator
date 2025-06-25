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

package proton.android.authenticator.features.imports.options.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.imports.options.presentation.ImportsOptionsModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.icons.ProviderIcon
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun ImportsOptionRow(optionModel: ImportsOptionsModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium)
    ) {
        ProviderIcon(
            icon = optionModel.icon,
            size = 40.dp
        )

        Text(
            modifier = Modifier.weight(weight = 1f, fill = true),
            text = optionModel.nameText.asString(),
            color = Theme.colorScheme.textNorm,
            style = Theme.typography.body1Regular
        )

        if (!optionModel.isSupported) {
            Icon(
                painter = painterResource(id = R.drawable.ic_exclamation_triangle_filled),
                contentDescription = null,
                tint = Theme.colorScheme.gradientBannerColor1
            )
        }
    }
}
