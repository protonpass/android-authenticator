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

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightText(
    text: String,
    textColor: Color,
    highlightedText: String,
    highlightedTextColor: Color,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val highlightedAnnotatedText = remember(
        keys = arrayOf(
            text,
            textColor,
            highlightedText,
            highlightedTextColor,
            textStyle,
            maxLines,
            overflow
        )
    ) {
        buildAnnotatedString {
            if (highlightedText.isEmpty()) {
                append(text)

                return@buildAnnotatedString
            }

            val lowerCaseText = text.lowercase()
            val lowerCaseHighlightedText = highlightedText.lowercase()

            if (!lowerCaseText.contains(lowerCaseHighlightedText)) {
                append(text)

                return@buildAnnotatedString
            }

            var currentIndex = 0
            var startIndex: Int

            val highlightedStyle = textStyle.copy(color = highlightedTextColor).toSpanStyle()

            while (true) {
                startIndex = lowerCaseText.indexOf(lowerCaseHighlightedText, currentIndex)

                if (startIndex == -1) {
                    break
                }

                val endIndex = startIndex.plus(lowerCaseHighlightedText.length)

                // Append text before the highlight
                append(text.substring(currentIndex, startIndex))

                // Append highlighted text
                withStyle(style = highlightedStyle) {
                    append(text.substring(startIndex, endIndex))
                }

                currentIndex = endIndex
            }

            // Append any remaining text after the last highlight
            if (currentIndex < text.length) {
                append(text.substring(currentIndex, text.length))
            }
        }
    }

    Text(
        modifier = modifier,
        text = highlightedAnnotatedText,
        color = textColor,
        maxLines = maxLines,
        overflow = overflow,
        style = textStyle
    )
}
