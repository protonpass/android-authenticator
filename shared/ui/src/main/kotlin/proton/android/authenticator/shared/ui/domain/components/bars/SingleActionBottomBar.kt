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

package proton.android.authenticator.shared.ui.domain.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.buttons.PrimaryActionButton
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
fun SingleActionBottomBar(
    actionText: String,
    onActionClick: () -> Unit,
    showProtonPrivacyBrand: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = ThemePadding.Large,
                end = ThemePadding.Large,
                bottom = ThemePadding.ExtraLarge
            ),
        verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.MediumLarge)
    ) {
        PrimaryActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = actionText,
            onClick = onActionClick
        )

        if (showProtonPrivacyBrand) {
            Icon(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.ic_proton_privacy),
                contentDescription = null,
                tint = Theme.colorScheme.textNorm
            )
        }
    }
}
