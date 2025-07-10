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

package proton.android.authenticator.business.entries.application.update

import proton.android.authenticator.business.entries.domain.EntryAlgorithm
import proton.android.authenticator.commonrust.AuthenticatorEntryModel
import proton.android.authenticator.commonrust.AuthenticatorEntrySteamCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorEntryTotpCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.infrastructure.commands.Command

sealed class UpdateEntryCommand : Command {

    internal abstract val id: String

    internal abstract val name: String

    internal abstract val secret: String

    internal abstract val note: String?

    internal abstract val position: Int

    internal abstract fun toModel(authenticatorClient: AuthenticatorMobileClientInterface): AuthenticatorEntryModel

    data class FromSteam(
        override val id: String,
        override val name: String,
        override val secret: String,
        override val note: String? = null,
        override val position: Int
    ) : UpdateEntryCommand() {

        override fun toModel(authenticatorClient: AuthenticatorMobileClientInterface): AuthenticatorEntryModel =
            authenticatorClient.newSteamEntryFromParams(
                params = AuthenticatorEntrySteamCreateParameters(
                    name = name,
                    secret = secret,
                    note = note
                )
            )
    }

    data class FromTotp(
        override val id: String,
        override val name: String,
        override val secret: String,
        override val note: String? = null,
        override val position: Int,
        internal val issuer: String,
        internal val period: Int,
        internal val digits: Int,
        internal val algorithm: EntryAlgorithm
    ) : UpdateEntryCommand() {

        override fun toModel(authenticatorClient: AuthenticatorMobileClientInterface): AuthenticatorEntryModel =
            authenticatorClient.newTotpEntryFromParams(
                params = AuthenticatorEntryTotpCreateParameters(
                    name = name,
                    secret = secret,
                    issuer = issuer,
                    period = period.toUShort(),
                    digits = digits.toUByte(),
                    algorithm = algorithm.asAuthenticatorEntryAlgorithm(),
                    note = note
                )
            )

    }

}
