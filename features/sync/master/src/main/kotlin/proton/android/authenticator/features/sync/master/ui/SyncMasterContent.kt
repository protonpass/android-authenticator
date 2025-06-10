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

package proton.android.authenticator.features.sync.master.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.sync.master.R
import proton.android.authenticator.features.sync.master.presentation.SyncMasterState
import proton.android.authenticator.shared.ui.domain.components.buttons.VerticalActionsButtons
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.domain.theme.isDarkTheme
import proton.android.authenticator.shared.ui.R as uiR

@Composable
internal fun SyncMasterContent(
    state: SyncMasterState.Ready,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) = with(state) {
    val placeholderResId = if (isDarkTheme(themeType = themeType)) {
        uiR.drawable.ic_placeholder_sync_dark
    } else {
        uiR.drawable.ic_placeholder_sync
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = ThemePadding.Small)
                .offset(y = -ThemeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(size = 240.dp),
                painter = painterResource(id = placeholderResId),
                contentDescription = null
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.sync_master_title),
                    textAlign = TextAlign.Center,
                    color = Theme.colorScheme.textNorm,
                    style = Theme.typography.subtitle
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.sync_master_description),
                    textAlign = TextAlign.Center,
                    style = Theme.typography.bodyRegular,
                    color = Theme.colorScheme.textWeak
                )
            }

            VerticalActionsButtons(
                primaryActionText = stringResource(id = R.string.sync_master_sign_up_button),
                onPrimaryActionClick = onSignUpClick,
                secondaryActionText = stringResource(id = R.string.sync_master_sign_in_button),
                onSecondaryActionClick = onSignInClick
            )
        }
    }
}
