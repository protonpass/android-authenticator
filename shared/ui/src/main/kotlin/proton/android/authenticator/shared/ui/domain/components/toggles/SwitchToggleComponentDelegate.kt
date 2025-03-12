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

package proton.android.authenticator.shared.ui.domain.components.toggles

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import proton.android.authenticator.shared.ui.domain.theme.Theme

internal class SwitchToggleComponentDelegate(
    override val renderId: String,
    private val modifier: Modifier,
    private val isChecked: Boolean,
    private val onCheckedChange: (Boolean) -> Unit
) : ToggleComponent {

    @Composable
    override fun Render() {
        Switch(
            modifier = modifier.testTag(tag = renderId),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors().copy(
                checkedThumbColor = Color.White,
                checkedTrackColor = Theme.colorScheme.signalSuccess,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Theme.colorScheme.textHint
            )
        )
    }

}
