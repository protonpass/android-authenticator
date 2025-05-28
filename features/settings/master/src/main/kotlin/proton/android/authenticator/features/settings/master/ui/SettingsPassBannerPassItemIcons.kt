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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun SettingsPassBannerPassItemIcons(modifier: Modifier = Modifier) {
    val passItems = listOf(
        Pair(
            first = UiIcon.Resource(id = R.drawable.ic_user),
            second = Theme.colorScheme.passItemLogin
        ),
        Pair(
            first = UiIcon.Resource(id = R.drawable.ic_alias),
            second = Theme.colorScheme.passItemAlias
        ),
        Pair(
            first = UiIcon.Resource(id = R.drawable.ic_file_lines),
            second = Theme.colorScheme.passItemNote
        ),
        Pair(
            first = UiIcon.Resource(id = R.drawable.ic_credit_card),
            second = Theme.colorScheme.passItemCard
        ),
        Pair(
            first = UiIcon.Resource(id = R.drawable.ic_key),
            second = Theme.colorScheme.passItemPassword
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
    ) {
        passItems.forEach { (icon, tintColor) ->
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(size = ThemeRadius.MediumSmall))
                    .background(color = tintColor.copy(alpha = 0.2f))
                    .padding(all = ThemePadding.Small)
            ) {
                Icon(
                    modifier = Modifier.size(size = 20.dp),
                    painter = icon.asPainter(),
                    contentDescription = null,
                    tint = tintColor
                )
            }
        }
    }
}
