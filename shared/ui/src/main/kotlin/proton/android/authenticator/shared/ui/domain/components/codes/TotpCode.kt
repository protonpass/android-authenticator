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

package proton.android.authenticator.shared.ui.domain.components.codes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.applyIf
import proton.android.authenticator.shared.ui.domain.modifiers.containerShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

private const val CODE_ANIMATION_ON_MILLIS = 1_000
private const val CODE_ANIMATION_OFF_MILLIS = 0

@Composable
fun TotpCode(
    codeText: UiText,
    animateCodeOnChange: Boolean,
    showBoxes: Boolean,
    showShadows: Boolean,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    separationWidth: Dp = ThemeSpacing.ExtraSmall
) {
    val codeChangeAnimationDuration = remember(key1 = animateCodeOnChange) {
        if (animateCodeOnChange) CODE_ANIMATION_ON_MILLIS else CODE_ANIMATION_OFF_MILLIS
    }

    var previousOtpCodeText by remember {
        mutableStateOf(codeText)
    }

    SideEffect {
        previousOtpCodeText = codeText
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = separationWidth)
    ) {
        val codeString = codeText.asString()
        val previousCodeString = previousOtpCodeText.asString()
        val codeStringLength = minOf(codeString.length, previousCodeString.length)

        for (i in 0 until codeStringLength) {
            val codeDigit = codeString[i]
            val previousCodeDigit = previousCodeString[i]
            val newCodeDigit = if (codeDigit == previousCodeDigit) {
                previousCodeDigit
            } else {
                codeDigit
            }

            Box(
                modifier = Modifier.applyIf(
                    condition = showBoxes,
                    ifTrue = { containerShadow() }
                )
            ) {
                AnimatedContent(
                    targetState = newCodeDigit,
                    transitionSpec = {
                        slideInVertically(
                            animationSpec = tween(durationMillis = codeChangeAnimationDuration)
                        ) { height ->
                            if (newCodeDigit > previousCodeDigit) height else -height
                        } togetherWith slideOutVertically(
                            animationSpec = tween(durationMillis = codeChangeAnimationDuration)
                        ) { height ->
                            if (newCodeDigit > previousCodeDigit) -height else height
                        }
                    }
                ) { codeDigit ->
                    if (!codeDigit.isWhitespace()) {
                        Text(
                            modifier = Modifier
                                .applyIf(
                                    condition = showBoxes,
                                    ifTrue = {
                                        padding(
                                            horizontal = ThemePadding.MediumSmall.div(2),
                                            vertical = ThemePadding.ExtraSmall
                                        )
                                    }
                                ),
                            text = codeDigit.toString(),
                            color = color,
                            style = if (showShadows) {
                                style.copy(shadow = ThemeShadow.TextDefault)
                            } else {
                                style
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(width = separationWidth))
                    }
                }
            }
        }
    }
}
