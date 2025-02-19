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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import proton.android.authenticator.shared.ui.R
import proton.android.authenticator.shared.ui.domain.models.UiText

internal class NetworkImageComponentDelegate(
    private val modifier: Modifier,
    private val url: String,
    private val contentDescription: UiText?
) : ImageComponent {

    @Composable
    override fun Render() {
        val context = LocalContext.current
        val model = remember(url) {
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build()
        }

        AsyncImage(
            modifier = modifier,
            model = model,
            contentDescription = contentDescription?.asString(),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_launcher_background)
        )
    }

}
