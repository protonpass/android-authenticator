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

package proton.android.authenticator.features.settings.master.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun SettingsNavigationRow(
    title: UiText,
    onClick: () -> Unit,
    description: UiText? = null,
    icon: UiIcon? = null
) {
    val verticalPadding = remember {
        ThemePadding.Small.plus(ThemePadding.MediumSmall)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = ThemePadding.Medium,
                vertical = verticalPadding
            ),
        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { icon ->
            Image(
                painter = icon.asPainter(),
                contentDescription = null
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall)
        ) {
            Text(
                text = title.asString(),
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.body1Regular
            )

            description?.let { text ->
                Text(
                    text = text.asString(),
                    color = Theme.colorScheme.textWeak,
                    style = Theme.typography.body2Regular
                )
            }
        }
    }
}
