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
    abstract val gradientBackgroundColor1: Color

    @Stable
    abstract val gradientBackgroundColor2: Color

    @Stable
    abstract val gradientBackgroundColor3: Color

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
    abstract val inputBorderFocused: Color

    @Stable
    abstract val interactionPurple: Color

    @Stable
    abstract val interactionPurpleNorm: Color

    @Stable
    abstract val interactionPurpleStrong: Color

    @Stable
    abstract val signalSuccess: Color

    @Stable
    abstract val textHint: Color

    @Stable
    abstract val textNorm: Color

    @Stable
    abstract val textWeak: Color

    @Immutable
    data object Dark : ThemeColors() {

        override val gradientBackgroundColor1: Color = Color(color = 0xFF3A3836)

        override val gradientBackgroundColor2: Color = Color(color = 0xFF252321)

        override val gradientBackgroundColor3: Color = Color(color = 0xFF171514)

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

        override val inputBorderFocused: Color = Color(color = 0xFFA779FF)

        override val interactionPurple: Color = Color(color = 0xFFCAAAFF)

        override val interactionPurpleNorm: Color = Color(color = 0xFF6D4AFF)

        override val interactionPurpleStrong: Color = Color(color = 0xFF24212B)

        override val signalSuccess: Color = Color(color = 0xFF4AB89A)

        override val textHint: Color = Color(color = 0xFF88859D)

        override val textNorm: Color = Color(color = 0xFFFFFFFF)

        override val textWeak: Color = Color(color = 0xFFDFDFDF)

    }

    @Immutable
    data object Light : ThemeColors() {

        override val gradientBackgroundColor1: Color = Color(color = 0xFF3A3836)

        override val gradientBackgroundColor2: Color = Color(color = 0xFF252321)

        override val gradientBackgroundColor3: Color = Color(color = 0xFF171514)

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

        override val inputBorderFocused: Color = Color(color = 0xFFA779FF)

        override val interactionPurple: Color = Color(color = 0xFFCAAAFF)

        override val interactionPurpleNorm: Color = Color(color = 0xFF6D4AFF)

        override val interactionPurpleStrong: Color = Color(color = 0xFF24212B)

        override val signalSuccess: Color = Color(color = 0xFF4AB89A)

        override val textHint: Color = Color(color = 0xFF88859D)

        override val textNorm: Color = Color(color = 0xFFFFFFFF)

        override val textWeak: Color = Color(color = 0xFFDFDFDF)

    }

}

internal val ThemeColorScheme: ProvidableCompositionLocal<ThemeColors> = staticCompositionLocalOf {
    ThemeColors.Light
}
