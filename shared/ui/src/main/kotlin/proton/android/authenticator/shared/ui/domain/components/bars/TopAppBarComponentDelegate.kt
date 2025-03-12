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

package proton.android.authenticator.shared.ui.domain.components.bars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent

internal class TopAppBarComponentDelegate(
    override val renderId: String,
    private val modifier: Modifier,
    private val title: TextComponent,
    private val actions: List<Component>
) : BarComponent {

    @[Composable OptIn(ExperimentalMaterial3Api::class)]
    override fun Render() {
        TopAppBar(
            modifier = modifier.testTag(tag = renderId),
            title = {
                title.Render()
            },
            actions = {
                actions.forEach { action ->
                    action.Render()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Transparent
            )
        )
    }

}
