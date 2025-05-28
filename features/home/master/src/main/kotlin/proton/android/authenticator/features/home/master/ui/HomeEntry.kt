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

package proton.android.authenticator.features.home.master.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.menus.SwipeRevealMenu
import proton.android.authenticator.shared.ui.domain.models.UiTextMask
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing
import proton.android.authenticator.shared.ui.domain.theme.ThemeType
import proton.android.authenticator.shared.ui.domain.theme.isDarkTheme

@Composable
internal fun HomeEntry(
    entryModel: HomeMasterEntryModel,
    entryCodeMasks: List<UiTextMask>,
    remainingSeconds: Int,
    animateOnCodeChange: Boolean,
    showBoxesInCode: Boolean,
    themeType: ThemeType,
    onCopyCodeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isDarkTheme(themeType = themeType)

    SwipeRevealMenu(
        modifier = modifier,
        isRevealed = true,
        gap = ThemeSpacing.Medium,
        leadingMenuContent = {
            HomeEntryAction(
                modifier = Modifier
                    .background(color = Theme.colorScheme.orangeAlpha20)
                    .clickable(onClick = onEditClick),
                iconResId = R.drawable.ic_pencil,
                actionResId = R.string.action_edit
            )
        },
        trailingMenuContent = {
            HomeEntryAction(
                modifier = Modifier
                    .background(color = Theme.colorScheme.redAlpha20)
                    .clickable(onClick = onDeleteClick),
                iconResId = R.drawable.ic_trash,
                actionResId = R.string.action_delete
            )
        }
    ) {
        HomeEntryCard(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(size = ThemeRadius.Medium))
                .backgroundSection(applyShadow = true)
                .clickable(onClick = onCopyCodeClick),
            entryModel = entryModel,
            entryCodeMasks = entryCodeMasks,
            remainingSeconds = remainingSeconds,
            animateOnCodeChange = animateOnCodeChange,
            showBoxesInCode = showBoxesInCode,
            showShadowsInTexts = isDarkTheme,
            showTextShadows = isDarkTheme,
            showIconBorder = !isDarkTheme
        )
    }
}
