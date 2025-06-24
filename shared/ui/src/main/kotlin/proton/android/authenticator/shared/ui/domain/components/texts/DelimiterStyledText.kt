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

package proton.android.authenticator.shared.ui.domain.components.texts

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun DelimiterStyledText(
    text: String,
    delimiter: String,
    textColor: Color,
    textStyle: TextStyle,
    delimitedTextStyle: TextStyle,
    delimitedTextColor: Color = textColor
) {
    var currentIndex = 0

    buildAnnotatedString {
        while (true) {
            val startIndex = text.indexOf(delimiter, startIndex = currentIndex)

            if (startIndex == -1) {
                append(text.substring(currentIndex))
                break
            }

            append(text.substring(currentIndex, startIndex))

            val innerStartIndex = startIndex.plus(delimiter.length)
            val endIndex = text.indexOf(delimiter, startIndex = innerStartIndex)

            if (endIndex == -1) {
                append(text.substring(startIndex))
                break
            }

            delimitedTextStyle
                .copy(color = delimitedTextColor)
                .toSpanStyle()
                .also { style ->
                    withStyle(style = style) {
                        append(delimiter)
                        append(text.substring(innerStartIndex, endIndex))
                        append(delimiter)
                    }
                }

            currentIndex = endIndex.plus(delimiter.length)
        }
    }.also { spannableString ->
        Text(
            text = spannableString,
            color = textColor,
            style = textStyle
        )
    }
}
