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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import me.proton.core.compose.theme.ProtonColors
import me.proton.core.compose.theme.ProtonShapes
import me.proton.core.compose.theme.ProtonTheme
import me.proton.core.compose.theme.isNightMode

@Composable
fun isDarkTheme(themeType: ThemeType): Boolean = when (themeType) {
    ThemeType.Dark -> true
    ThemeType.Light -> false
    ThemeType.System -> isSystemInDarkTheme()
}

@Composable
fun Theme(isDarkTheme: Boolean = isNightMode(), content: @Composable () -> Unit) {
    val colorScheme = remember(key1 = isDarkTheme) {
        if (isDarkTheme) ThemeColors.Dark
        else ThemeColors.Light
    }

    val protonColors = remember(key1 = isDarkTheme) {
        if (isDarkTheme) ProtonColors.Dark
        else ProtonColors.Light
    }

    CompositionLocalProvider(
        LocalThemeColorScheme provides colorScheme,
        LocalThemeTypographyScheme provides ThemeTypography
    ) {
        ProtonTheme(
            isDark = isDarkTheme,
            colors = protonColors.copy(
                backgroundNorm = Color.Transparent,
                backgroundSecondary = colorScheme.inputBackground,
                interactionNorm = colorScheme.buttonGradientTop,
                interactionStrongNorm = Color.Red,
                textAccent = colorScheme.accent,
                textHint = colorScheme.textHint,
                textNorm = colorScheme.textNorm,
                textWeak = colorScheme.textWeak
            ),
            shapes = ProtonShapes().copy(
                small = RoundedCornerShape(size = ThemeRadius.Small.plus(ThemeRadius.Small)),
                medium = RoundedCornerShape(size = ThemeRadius.Medium.plus(ThemeRadius.Small)),
                large = RoundedCornerShape(size = ThemeRadius.Large.plus(ThemeRadius.Small))
            ),
            content = content
        )
    }
}

object Theme {

    val colorScheme: ThemeColors
        @[Composable ReadOnlyComposable]
        get() = LocalThemeColorScheme.current

    val typography: ThemeTypography
        @[Composable ReadOnlyComposable]
        get() = LocalThemeTypographyScheme.current

}
