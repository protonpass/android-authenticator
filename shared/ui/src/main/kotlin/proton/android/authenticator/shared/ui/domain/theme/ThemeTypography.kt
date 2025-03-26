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

package proton.android.authenticator.shared.ui.domain.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ThemeTypography {

    @Stable
    val bodyRegular: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 22.sp
        )

    @Stable
    val body1Regular: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

    @Stable
    val body2Regular: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

    @Stable
    val body1Medium: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

    @Stable
    val body2Medium: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

    @Stable
    val captionRegular: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 14.sp
        )

    @Stable
    val compactMedium: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

    @Stable
    val emphasized: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

    @Stable
    val header: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 14.sp
        )

    @Stable
    val headline: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp
        )

    @Stable
    val monoMedium1: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp
        )

    @Stable
    val monoMedium2: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 14.sp
        )

    @Stable
    val monoNorm1: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 20.sp
        )

    @Stable
    val monoNorm2: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 18.sp
        )

    @Stable
    val title: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp
        )

    @Stable
    val subtitle: TextStyle
        @[Composable ReadOnlyComposable]
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 38.sp
        )

}

val ThemeTypographyScheme: ProvidableCompositionLocal<ThemeTypography> =
    staticCompositionLocalOf {
        ThemeTypography
    }
