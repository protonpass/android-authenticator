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

package proton.android.authenticator.shared.ui.domain.components.texts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import proton.android.authenticator.shared.ui.domain.components.containers.ContainerComponent
import proton.android.authenticator.shared.ui.domain.models.UiText
import proton.android.authenticator.shared.ui.domain.modifiers.containerShadow
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeSpacing

internal class TotpTextComponentDelegate(
    private val modifier: Modifier,
    private val text: UiText,
    private val color: @Composable () -> Color,
    private val style: @Composable () -> TextStyle
) : TextComponent {

    @Composable
    override fun Render() {
        val totp = text.asString()

        ContainerComponent.Horizontal(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(space = ThemeSpacing.ExtraSmall),
            contents = {
                buildList {
                    totp.map { digit ->
                        ContainerComponent.Box(
                            modifier = Modifier.containerShadow(),
                            contents = {
                                listOf(
                                    TextComponent.Standard(
                                        modifier = Modifier.padding(
                                            horizontal = ThemePadding.MediumSmall.div(2),
                                            vertical = ThemePadding.ExtraSmall
                                        ),
                                        text = UiText.Dynamic(value = digit.toString()),
                                        color = { color() },
                                        style = { style() }
                                    )
                                )
                            }
                        ).also(::add)
                    }
                }

            }
        ).Render()
    }

}
