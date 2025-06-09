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

package proton.android.authenticator.shared.ui.domain.components.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.ThemePadding
import proton.android.authenticator.shared.ui.domain.theme.ThemeRadius
import proton.android.authenticator.shared.ui.domain.theme.ThemeThickness

@Composable
fun EntryIcon(
    url: String,
    issuer: String,
    showIconBorder: Boolean,
    size: Dp = 36.dp
) {
    Box(
        modifier = Modifier
            .size(size = size)
            .clip(shape = RoundedCornerShape(size = ThemeRadius.Small))
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .diskCacheKey(url)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCacheKey(url)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .size(size.value.toInt())
                .build(),
            contentDescription = null,
            loading = {
                GradientCharacterIcon(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Theme.colorScheme.iconBackground)
                        .border(
                            width = ThemeThickness.Small,
                            color = Theme.colorScheme.iconBorder,
                            shape = RoundedCornerShape(size = ThemeRadius.Small)
                        ),
                    text = issuer
                )
            },
            error = {
                GradientCharacterIcon(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Theme.colorScheme.iconBackground)
                        .border(
                            width = ThemeThickness.Small,
                            color = Theme.colorScheme.iconBorder,
                            shape = RoundedCornerShape(size = ThemeRadius.Small)
                        ),
                    text = issuer
                )
            },
            success = {
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Theme.colorScheme.white)
                        .then(
                            if (showIconBorder) {
                                Modifier.border(
                                    width = ThemeThickness.Small,
                                    color = Theme.colorScheme.iconBorder,
                                    shape = RoundedCornerShape(size = ThemeRadius.Small)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(all = ThemePadding.ExtraSmall)
                )
            }
        )
    }
}
