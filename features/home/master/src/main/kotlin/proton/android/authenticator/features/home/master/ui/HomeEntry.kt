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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.codes.TotpCode
import proton.android.authenticator.shared.ui.domain.components.dividers.DoubleHorizontalDivider
import proton.android.authenticator.shared.ui.domain.components.icons.EntryIcon
import proton.android.authenticator.shared.ui.domain.components.indicators.TotpProgressIndicator
import proton.android.authenticator.shared.ui.domain.components.menus.SwipeRevealMenu
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundSection
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntry(
    entryModel: HomeMasterEntryModel,
    animateOnCodeChange: Boolean,
    showBoxesInCode: Boolean,
    showShadowsInTexts: Boolean,
    onCopyCodeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val showTextShadows = isSystemInDarkTheme() || showShadowsInTexts

    SwipeRevealMenu(
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
                actionResId = R.string.action_Delete
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
            animateOnCodeChange = animateOnCodeChange,
            showBoxesInCode = showBoxesInCode,
            showShadowsInTexts = showShadowsInTexts,
            showTextShadows = showTextShadows
        )
    }
}

@Composable
private fun HomeEntryCard(
    animateOnCodeChange: Boolean,
    showBoxesInCode: Boolean,
    showShadowsInTexts: Boolean,
    entryModel: HomeMasterEntryModel,
    showTextShadows: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(
                start = ThemePadding.Medium,
                top = ThemePadding.Medium,
                end = ThemePadding.Medium
            ),
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EntryIcon(
                issuer = entryModel.issuerText
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = entryModel.issuerText.asString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Theme.colorScheme.textNorm,
                    style = if (showTextShadows) {
                        Theme.typography.body1Regular.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.body1Regular
                    }
                )

                Text(
                    text = entryModel.nameText.asString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Theme.colorScheme.textWeak,
                    style = if (showTextShadows) {
                        Theme.typography.body2Regular.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.body2Regular
                    }
                )
            }

            TotpProgressIndicator(
                remainingSeconds = entryModel.remainingSeconds,
                totalSeconds = entryModel.totalSeconds,
                showShadowInCounter = showShadowsInTexts
            )
        }

        DoubleHorizontalDivider(
            modifier = Modifier.padding(horizontal = ThemePadding.Small)
        )

        Row(
            modifier = Modifier.padding(
                start = ThemePadding.Medium,
                end = ThemePadding.Medium,
                bottom = ThemePadding.Medium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TotpCode(
                modifier = Modifier.weight(weight = 1f, fill = true),
                codeText = entryModel.currentCodeText,
                animateCodeOnChange = animateOnCodeChange,
                showBoxes = showBoxesInCode,
                showShadows = showTextShadows,
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.monoMedium1
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(id = R.string.action_next),
                    color = Theme.colorScheme.textWeak,
                    style = if (showTextShadows) {
                        Theme.typography.body1Regular.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.body1Regular
                    }
                )

                Text(
                    text = entryModel.nextCodeText.asString(),
                    color = Theme.colorScheme.textNorm,
                    style = if (showTextShadows) {
                        Theme.typography.monoMedium2.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.monoMedium2
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeEntryAction(
    @DrawableRes iconResId: Int,
    @StringRes actionResId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.defaultMinSize(
            minWidth = 130.dp,
            minHeight = 122.dp
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = ThemeSpacing.Small)
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = Theme.colorScheme.textNorm
            )

            Text(
                text = stringResource(id = actionResId),
                color = Theme.colorScheme.textNorm,
                style = Theme.typography.captionRegular
            )
        }
    }
}
