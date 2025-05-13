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

package proton.android.authenticator.protonapps.domain

enum class ProtonAppType(
    internal val id: String,
    internal val url: String,
    internal val position: Int
) {
    Calendar(
        id = "me.proton.android.calendar",
        url = "https://proton.me/calendar/download",
        position = 3
    ),
    Drive(
        id = "me.proton.android.drive",
        url = "https://proton.me/drive/download",
        position = 4
    ),
    Mail(
        id = "ch.protonmail.android",
        url = "https://proton.me/mail/download",
        position = 2
    ),
    Pass(
        id = "proton.android.pass",
        url = "https://proton.me/pass/download/android",
        position = 0
    ),
    Vpn(
        id = "ch.protonvpn.android",
        url = "https://protonvpn.com/download-android",
        position = 1
    )
}
