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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    textStyle: TextStyle = LocalTextStyle.current,
    highlightedWord: String,
    highlightedStyle: SpanStyle,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val annotatedString = buildAnnotatedString {
        val lowerCaseText = text.lowercase()
        val lowerCaseHighlightedWord = highlightedWord.lowercase()

        if (highlightedWord.isEmpty() || !lowerCaseText.contains(lowerCaseHighlightedWord)) {
            append(text)
            return@buildAnnotatedString
        }

        val startIndex = lowerCaseText.indexOf(lowerCaseHighlightedWord)

        if (startIndex < 0) {
            append(text)
            return@buildAnnotatedString
        }

        val endIndex = startIndex + lowerCaseHighlightedWord.length

        append(text.substring(0, startIndex))

        withStyle(style = highlightedStyle) {
            append(text.substring(startIndex, endIndex))
        }

        append(text.substring(endIndex, text.length))
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        color = textColor,
        maxLines = maxLines,
        overflow = overflow,
        style = textStyle
    )
}
