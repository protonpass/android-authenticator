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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.contents.Content
import proton.android.authenticator.shared.ui.domain.screens.Screen
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding

@Suppress("MagicNumber")
abstract class ScaffoldScreen : Screen {

    abstract val bodyContents: List<Content>

    @Composable
    override fun Render() {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A0C2B),
                            Color(0xFF23063F),
                            Color(0xFF26093F)
                        ),
                        startY = -400f
                    )
                ),
            containerColor = Color.Transparent,
            topBar = {

            },
            bottomBar = {

            }
        ) { innerPaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = ThemePadding.Medium),
                contentPadding = innerPaddingValues
            ) {
                items(bodyContents) { bodyContent ->
                    bodyContent.Render()

                    Spacer(modifier = Modifier.height(height = ThemePadding.Small))
                }
            }
        }
    }

}
