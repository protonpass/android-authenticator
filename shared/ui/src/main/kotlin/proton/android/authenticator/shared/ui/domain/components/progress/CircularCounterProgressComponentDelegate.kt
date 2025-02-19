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

package proton.android.authenticator.shared.ui.domain.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.components.texts.TextComponent
import proton.android.authenticator.shared.ui.domain.models.UiText

internal class CircularCounterProgressComponentDelegate(
    private val modifier: Modifier,
    private val current: Int,
    private val total: Int
) : ProgressComponent {

    @Composable
    override fun Render() {
        val progress = remember(current, total) {
            current.toFloat() / total.toFloat()
        }

        ContainerComponent.Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            contents = {
                listOf(
                    ProgressComponent.Circular(
                        color = Color.Green,
                        progress = progress
                    ),
                    TextComponent.Title(
                        text = UiText.Dynamic(value = current.toString())
                    )
                )
            }
        ).Render()
    }

}
