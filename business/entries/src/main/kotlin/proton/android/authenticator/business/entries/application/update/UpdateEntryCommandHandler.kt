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

import proton.android.authenticator.commonrust.AuthenticatorEntrySteamCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorEntryTotpCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class UpdateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val updater: EntryUpdater
) : CommandHandler<UpdateEntryCommand> {

    override suspend fun handle(command: UpdateEntryCommand) {
        when (command) {
            is UpdateEntryCommand.FromSteam -> {
                authenticatorClient.newSteamEntryFromParams(
                    params = AuthenticatorEntrySteamCreateParameters(
                        name = command.name,
                        secret = command.secret,
                        note = command.note
                    )
                )
            }

            is UpdateEntryCommand.FromTotp -> {
                authenticatorClient.newTotpEntryFromParams(
                    params = AuthenticatorEntryTotpCreateParameters(
                        name = command.name,
                        secret = command.secret,
                        issuer = command.issuer,
                        period = command.period.toUShort(),
                        digits = command.digits.toUByte(),
                        algorithm = command.algorithm.asAuthenticatorEntryAlgorithm(),
                        note = command.note
                    )
                )
            }
        }.let { model ->
            updater.update(
                model = model.copy(id = command.id),
                params = authenticatorClient.getTotpParams(model)
            )
        }
    }

}
