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

package proton.android.authenticator.business.shared.telemetry

sealed class AuthenticatorTelemetryEvent {
    data class CreateEntry(val source: Source) : AuthenticatorTelemetryEvent() {
        enum class Source(val value: String) {
            Manual("manual"),
            ReadFromExternal("read_from_external")
        }
    }
    data object CopyCode : AuthenticatorTelemetryEvent()
    data object RemoveEntry : AuthenticatorTelemetryEvent()
    data object ReorderEntry : AuthenticatorTelemetryEvent()
    data class Import(val source: Source, val entriesCount: Int) : AuthenticatorTelemetryEvent() {
        enum class Source(val value: String) {
            Aegis("aegis"),
            Bitwarden("bitwarden"),
            Ente("ente"),
            Google("google"),
            LastPass("lastpass"),
            ProtonAuthenticator("proton_authenticator"),
            ProtonPass("proton_pass"),
            TwoFas("2fas")
        }
    }
    data object Export : AuthenticatorTelemetryEvent()
    data object OpenApp : AuthenticatorTelemetryEvent()
}
