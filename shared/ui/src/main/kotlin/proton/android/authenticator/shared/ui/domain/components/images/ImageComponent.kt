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

package proton.android.authenticator.shared.ui.domain.components.images

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import proton.android.authenticator.shared.ui.domain.components.Component
import proton.android.authenticator.shared.ui.domain.models.UiIcon
import proton.android.authenticator.shared.ui.domain.models.UiImage
import proton.android.authenticator.shared.ui.domain.models.UiText

internal sealed interface ImageComponent : Component {

    data class Icon(
        private val icon: UiIcon,
        private val modifier: Modifier = Modifier,
        private val contentDescription: UiText? = null,
        private val alignment: Alignment = Alignment.Center
    ) : ImageComponent by IconImageComponentDelegate(
        modifier = modifier,
        icon = icon,
        contentDescription = contentDescription,
        alignment = alignment
    )

    data class Local(
        private val image: UiImage,
        private val modifier: Modifier = Modifier,
        private val contentDescription: UiText? = null,
        private val alignment: Alignment = Alignment.Center
    ) : ImageComponent by LocalImageComponentDelegate(
        modifier = modifier,
        image = image,
        contentDescription = contentDescription,
        alignment = alignment
    )

    data class Network(
        private val url: String,
        private val modifier: Modifier = Modifier,
        private val contentDescription: UiText? = null
    ) : ImageComponent by NetworkImageComponentDelegate(
        modifier = modifier,
        url = url,
        contentDescription = contentDescription
    )

}
