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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

sealed class ThemeColors {

    @Stable
    abstract val accent: Color

    @Stable
    abstract val actionButtonBackgroundGradientTop: Color

    @Stable
    abstract val actionButtonBackgroundGradientBottom: Color

    @Stable
    abstract val actionButtonBorderGradientTop: Color

    @Stable
    abstract val actionButtonBorderGradientBottom: Color

    @Stable
    abstract val backgroundButtonBorderWeak: Color

    @Stable
    abstract val backgroundDropdown: Color

    @Stable
    abstract val backgroundGradientTop: Color

    @Stable
    abstract val backgroundGradientBottom: Color

    @Stable
    abstract val backgroundTopBar: Color

    @Stable
    abstract val buttonGradientTop: Color

    @Stable
    abstract val buttonGradientBottom: Color

    @Stable
    abstract val gradientBannerColor1: Color

    @Stable
    abstract val gradientBannerColor2: Color

    @Stable
    abstract val gradientBannerColor3: Color

    @Stable
    abstract val gradientBannerColor4: Color

    @Stable
    abstract val gradientBannerColor5: Color

    @Stable
    abstract val gradientBannerColor6: Color

    @Stable
    abstract val gradientBannerColor7: Color

    @Stable
    abstract val gradientBannerColor8: Color

    @Stable
    abstract val gradientBannerColor9: Color

    @Stable
    abstract val gradientBannerColor10: Color

    @Stable
    abstract val gradientButtonColor1: Color

    @Stable
    abstract val gradientButtonColor2: Color

    @Stable
    abstract val gradientTopBarColor1: Color

    @Stable
    abstract val gradientTopBarColor2: Color

    @Stable
    abstract val iconBackground: Color

    @Stable
    abstract val iconBorder: Color

    @Stable
    abstract val inputBackground: Color

    @Stable
    abstract val inputBorder: Color

    @Stable
    abstract val inputBorderFocused: Color

    @Stable
    abstract val interactionPurple: Color

    @Stable
    abstract val interactionPurpleNorm: Color

    @Stable
    abstract val menuListBackground: Color

    @Stable
    abstract val menuListBorder: Color

    @Stable
    abstract val signalError: Color

    @Stable
    abstract val signalDanger: Color

    @Stable
    abstract val signalSuccess: Color

    @Stable
    abstract val signalWarning: Color

    @Stable
    abstract val surface: Color

    @Stable
    abstract val surfaceContainerHigh: Color

    @Stable
    abstract val surfaceVariant: Color

    @Stable
    abstract val textHint: Color

    @Stable
    abstract val textNorm: Color

    @Stable
    abstract val textWeak: Color

    @Stable
    val aux: Color = Color(color = 0xB2191927)

    @Stable
    val black: Color = Color(color = 0x00000000)

    @Stable
    val blackAlpha8: Color = Color(color = 0x14000000)

    @Stable
    val blackAlpha10: Color = Color(color = 0x19000000)

    @Stable
    val blackAlpha12: Color = Color(color = 0x1F000000)

    @Stable
    val blackAlpha20: Color = Color(color = 0x33000000)

    @Stable
    val orangeAlpha20: Color = Color(color = 0x33FF8C00)

    @Stable
    val passItemAlias: Color = Color(color = 0xFF6ABDB3)

    @Stable
    val passItemCard: Color = Color(color = 0xFF91DC9C)

    @Stable
    val passItemLogin: Color = Color(color = 0xFFA779FF)

    @Stable
    val passItemNote: Color = Color(color = 0xFFFFCA8A)

    @Stable
    val passItemPassword: Color = Color(color = 0xFFFC9C9F)

    @Stable
    val purpleAlpha25: Color = Color(color = 0x40995EFF)

    @Stable
    val redAlpha20: Color = Color(color = 0x33FF0000)

    @Stable
    val white: Color = Color(color = 0xFFFFFFFF)

    @Stable
    val whiteAlpha12: Color = Color(color = 0x1FFFFFFF)

    @Stable
    val whiteAlpha20: Color = Color(color = 0x33FFFFFF)

    @Stable
    val whiteAlpha25: Color = Color(color = 0x40FFFFFF)

    @Stable
    val whiteAlpha30: Color = Color(color = 0x4CFFFFFF)

    @Stable
    val whiteAlpha70: Color = Color(color = 0xB2FFFFFF)

    @Immutable
    data object Dark : ThemeColors() {

        override val accent: Color = Color(color = 0xFFB080FF)

        override val actionButtonBackgroundGradientTop: Color = Color(color = 0x1FFFFFFF)

        override val actionButtonBackgroundGradientBottom: Color = Color(color = 0x1FFFFFFF)

        override val actionButtonBorderGradientTop: Color = Color(color = 0x33FFFFFF)

        override val actionButtonBorderGradientBottom: Color = Color(color = 0x02FFFFFF)

        override val backgroundButtonBorderWeak: Color = Color(color = 0x52FFFFFF)

        override val backgroundDropdown: Color = Color(color = 0x1FFFFFFF)

        override val backgroundGradientTop: Color = Color(color = 0xFF2D2A28)

        override val backgroundGradientBottom: Color = Color(color = 0xFF161514)

        override val backgroundTopBar: Color = Color(color = 0xF9252525)

        override val buttonGradientTop: Color = Color(color = 0xFF7350FF)

        override val buttonGradientBottom: Color = Color(color = 0xFF453099)

        override val gradientBannerColor1: Color = Color(color = 0xFFFFD580)

        override val gradientBannerColor2: Color = Color(color = 0xFFF6C592)

        override val gradientBannerColor3: Color = Color(color = 0xFFEBB6A2)

        override val gradientBannerColor4: Color = Color(color = 0xFFDFA5AF)

        override val gradientBannerColor5: Color = Color(color = 0xFFD397BE)

        override val gradientBannerColor6: Color = Color(color = 0xFFC486CB)

        override val gradientBannerColor7: Color = Color(color = 0xFFB578D9)

        override val gradientBannerColor8: Color = Color(color = 0xFFA166E5)

        override val gradientBannerColor9: Color = Color(color = 0xFF8B57F2)

        override val gradientBannerColor10: Color = Color(color = 0xFF704CFF)

        override val gradientButtonColor1: Color = Color(color = 0xFF7350FF)

        override val gradientButtonColor2: Color = Color(color = 0xFF453099)

        override val gradientTopBarColor1: Color = Color(color = 0xFF252321)

        override val gradientTopBarColor2: Color = Color(color = 0xFF3A3836)

        override val iconBackground: Color = Color(color = 0xFF24212B)

        override val iconBorder: Color = Color(color = 0xFF24212B)

        override val inputBackground: Color = Color(color = 0x7F000000)

        override val inputBorder: Color = Color(color = 0x1FFFFFFF)

        override val inputBorderFocused: Color = Color(color = 0xFFA779FF)

        override val interactionPurple: Color = Color(color = 0xFFCAAAFF)

        override val interactionPurpleNorm: Color = Color(color = 0xFF6D4AFF)

        override val menuListBackground: Color = Color(color = 0xFF373535)

        override val menuListBorder: Color = Color(color = 0x1FFFFFFF)

        override val signalError: Color = Color(color = 0xFFF08FA4)

        override val signalDanger: Color = Color(color = 0xFFFF7979)

        override val signalSuccess: Color = Color(color = 0xFF4AB89A)

        override val signalWarning: Color = Color(color = 0xFFFFB879)

        override val surface: Color = Color(color = 0xFFE6E0E9)

        override val surfaceContainerHigh: Color = Color(color = 0xFF2B2930)

        override val surfaceVariant: Color = Color(color = 0xFFCAC4D0)

        override val textHint: Color = Color(color = 0xFF88859D)

        override val textNorm: Color = Color(color = 0xFFFFFFFF)

        override val textWeak: Color = Color(color = 0x99FFFFFF)

    }

    @Immutable
    data object Light : ThemeColors() {

        override val accent: Color = Color(color = 0xFF6D4AFF)

        override val actionButtonBackgroundGradientTop: Color = Color(color = 0xFFF2F2F1)

        override val actionButtonBackgroundGradientBottom: Color = Color(color = 0xFFFFFFFF)

        override val actionButtonBorderGradientTop: Color = Color(color = 0xFFF2F2F1)

        override val actionButtonBorderGradientBottom: Color = Color(color = 0xFFFFFFFF)

        override val backgroundButtonBorderWeak: Color = Color(color = 0x2E000000)

        override val backgroundDropdown: Color = Color(color = 0xFFFFFFFF)

        override val backgroundGradientTop: Color = Color(color = 0xFFF5F5F4)

        override val backgroundGradientBottom: Color = Color(color = 0xFFFAFAF9)

        override val backgroundTopBar: Color = Color(color = 0xF9E9E9E6)

        override val buttonGradientTop: Color = Color(color = 0xFF7350FF)

        override val buttonGradientBottom: Color = Color(color = 0xFF453099)

        override val gradientBannerColor1: Color = Color(color = 0xFFFFD580)

        override val gradientBannerColor2: Color = Color(color = 0xFFF6C592)

        override val gradientBannerColor3: Color = Color(color = 0xFFEBB6A2)

        override val gradientBannerColor4: Color = Color(color = 0xFFDFA5AF)

        override val gradientBannerColor5: Color = Color(color = 0xFFD397BE)

        override val gradientBannerColor6: Color = Color(color = 0xFFC486CB)

        override val gradientBannerColor7: Color = Color(color = 0xFFB578D9)

        override val gradientBannerColor8: Color = Color(color = 0xFFA166E5)

        override val gradientBannerColor9: Color = Color(color = 0xFF8B57F2)

        override val gradientBannerColor10: Color = Color(color = 0xFF704CFF)

        override val gradientButtonColor1: Color = Color(color = 0xFF7350FF)

        override val gradientButtonColor2: Color = Color(color = 0xFF453099)

        override val gradientTopBarColor1: Color = Color(color = 0xFF252321)

        override val gradientTopBarColor2: Color = Color(color = 0xFF3A3836)

        override val iconBackground: Color = Color(color = 0xFFF3EEF7)

        override val iconBorder: Color = Color(color = 0x2D512877)

        override val inputBackground: Color = Color(color = 0x06000000)

        override val inputBorder: Color = Color(color = 0x10000000)

        override val inputBorderFocused: Color = Color(color = 0xFFA779FF)

        override val interactionPurple: Color = Color(color = 0xFFCAAAFF)

        override val interactionPurpleNorm: Color = Color(color = 0xFF6D4AFF)

        override val menuListBackground: Color = Color(color = 0xFFFFFFFF)

        override val menuListBorder: Color = Color(color = 0xFFECECEC)

        override val signalError: Color = Color(color = 0xFFCC2D4F)

        override val signalDanger: Color = Color(color = 0xFFFF7979)

        override val signalSuccess: Color = Color(color = 0xFF4AB89A)

        override val signalWarning: Color = Color(color = 0xFFFFB879)

        override val surface: Color = Color(color = 0xFF1D1B20)

        override val surfaceContainerHigh: Color = Color(color = 0xFFECE6F0)

        override val surfaceVariant: Color = Color(color = 0xFF49454F)

        override val textHint: Color = Color(color = 0xFF88859D)

        override val textNorm: Color = Color(color = 0xFF44403C)

        override val textWeak: Color = Color(color = 0xFF78716C)

    }

}

internal val LocalThemeColorScheme: ProvidableCompositionLocal<ThemeColors> = staticCompositionLocalOf {
    ThemeColors.Light
}
