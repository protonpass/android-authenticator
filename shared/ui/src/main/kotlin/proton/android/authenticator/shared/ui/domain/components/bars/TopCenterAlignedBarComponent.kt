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

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.components.buttons.ButtonComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.theme.Theme

internal class TopCenterAlignedBarComponent(
    private val modifier: Modifier,
    private val title: UiText,
    private val navigationIcon: UiIcon? = null,
    private val onNavigationClick: () -> Unit = {}
) : BarComponent {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Render() {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                TextComponent.Standard(
                    text = title,
                    color = { Theme.colorScheme.textNorm },
                    style = { Theme.typography.emphasized }
                ).Render()
            },
            navigationIcon = {
                navigationIcon?.let { icon ->
                    ButtonComponent.Icon(
                        icon = icon,
                        tint = { Theme.colorScheme.interactionPurple },
                        onClick = onNavigationClick
                    ).Render()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Transparent
            )
        )
    }

}
