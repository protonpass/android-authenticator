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

package proton.android.authenticator.shared.common.domain.builds

enum class BuildFlavor(val value: String, val type: BuildFlavorType) {
    AlphaBlack(value = "alphaBlack", type = BuildFlavorType.Alpha),
    AlphaProd(value = "alphaProd", type = BuildFlavorType.Alpha),
    DevBlack(value = "devBlack", type = BuildFlavorType.Dev),
    DevProd(value = "devProd", type = BuildFlavorType.Dev),
    FdroidBlack(value = "fdroidBlack", type = BuildFlavorType.Fdroid),
    FdroidProd(value = "fdroidProd", type = BuildFlavorType.Fdroid),
    PlayBlack(value = "playBlack", type = BuildFlavorType.PlayStore),
    PlayProd(value = "playProd", type = BuildFlavorType.PlayStore);

    companion object {

        fun from(value: String): BuildFlavor = when (value) {
            AlphaBlack.value -> AlphaBlack
            AlphaProd.value -> AlphaProd
            DevBlack.value -> DevBlack
            DevProd.value -> DevProd
            FdroidBlack.value -> FdroidBlack
            FdroidProd.value -> FdroidProd
            PlayBlack.value -> PlayBlack
            PlayProd.value -> PlayProd
            else -> throw UnsupportedOperationException("Unsupported flavour")
        }

    }

}
