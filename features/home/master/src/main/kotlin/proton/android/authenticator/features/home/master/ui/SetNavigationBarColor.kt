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

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetNavigationBarColor(color: Color, useDarkIcons: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    DisposableEffect(activity.window) {
        val window = activity.window
        val originalColor = window.navigationBarColor

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, view)
        val originalIsLight = insetsController.isAppearanceLightNavigationBars

        window.navigationBarColor = color.toArgb()
        insetsController.isAppearanceLightNavigationBars = useDarkIcons

        onDispose {
            window.navigationBarColor = originalColor
            insetsController.isAppearanceLightNavigationBars = originalIsLight
        }
    }
}
