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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.components.codes.TotpCode
import proton.android.authenticator.shared.ui.domain.components.dividers.DoubleHorizontalDivider
import proton.android.authenticator.shared.ui.domain.components.icons.EntryIcon
import proton.android.authenticator.shared.ui.domain.components.indicators.TotpProgressIndicator
import proton.android.authenticator.shared.ui.domain.components.texts.HighlightText
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.models.UiTextMask
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

@Composable
internal fun HomeEntryCard(
    animateOnCodeChange: Boolean,
    showBoxesInCode: Boolean,
    showShadowsInTexts: Boolean,
    showIconBorder: Boolean,
    entryModel: HomeMasterEntryModel,
    searchQuery: String,
    entryCodeMasks: List<UiTextMask>,
    remainingSeconds: Int,
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
                url = entryModel.iconUrl,
                issuer = entryModel.issuer,
                showIconBorder = showIconBorder
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            ) {
                HighlightText(
                    text = entryModel.issuer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textColor = Theme.colorScheme.textNorm,
                    textStyle = if (showTextShadows) {
                        Theme.typography.body1Regular.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.body1Regular
                    },
                    highlightedWord = searchQuery,
                    highlightedStyle = SpanStyle(color = Theme.colorScheme.accent)
                )

                HighlightText(
                    text = entryModel.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textColor = Theme.colorScheme.textWeak,
                    textStyle = if (showTextShadows) {
                        Theme.typography.body2Regular.copy(shadow = ThemeShadow.TextDefault)
                    } else {
                        Theme.typography.body2Regular
                    },
                    highlightedWord = searchQuery,
                    highlightedStyle = SpanStyle(color = Theme.colorScheme.accent)
                )
            }

            TotpProgressIndicator(
                remainingSeconds = remainingSeconds,
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
                codeText = UiText.Dynamic(
                    value = entryModel.currentCode,
                    masks = entryCodeMasks
                ),
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
                    text = UiText.Dynamic(
                        value = entryModel.nextCode,
                        masks = entryCodeMasks
                    ).asString(),
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
