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

package proton.android.authenticator.shared.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.modifiers.backgroundScreenGradient
import proton.android.authenticator.shared.ui.domain.screens.Screen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

abstract class ScaffoldScreen : Screen {

    abstract val topBarContent: Content?

    abstract val bodyContents: List<Content>

    abstract val bottomBarContent: Content?

    @Composable
    override fun Render() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .backgroundScreenGradient(),
            containerColor = Color.Transparent,
            topBar = {
                topBarContent?.Render()
            },
            bottomBar = {
                bottomBarContent?.Render()
            }
        ) { innerPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPaddingValues)
                    .padding(all = ThemePadding.Medium)
            ) {
                bodyContents.forEach { bodyContent ->
                    key(bodyContent.renderId) {
                        bodyContent.Render()
                    }
                }
            }
        }
    }

}
