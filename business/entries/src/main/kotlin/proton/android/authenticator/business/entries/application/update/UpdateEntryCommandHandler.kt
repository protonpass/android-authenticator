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
import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class UpdateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val updater: EntryUpdater
) : CommandHandler<UpdateEntryCommand, Unit, UpdateEntryReason> {

    override suspend fun handle(command: UpdateEntryCommand): Answer<Unit, UpdateEntryReason> = try {
        when (command) {
            is UpdateEntryCommand.FromSteam -> command.toModel()
            is UpdateEntryCommand.FromTotp -> command.toModel()
        }
            .let { model -> updater.update(command.id, command.position, model) }
            .let(Answer<Unit, UpdateEntryReason>::Success)
    } catch (_: AuthenticatorException) {
        Answer.Failure(reason = UpdateEntryReason.InvalidEntrySecret)
    } catch (_: IllegalStateException) {
        Answer.Failure(reason = UpdateEntryReason.EntryNotFound)
    }

    private fun UpdateEntryCommand.FromSteam.toModel() = authenticatorClient.newSteamEntryFromParams(
        params = AuthenticatorEntrySteamCreateParameters(
            name = name,
            secret = secret,
            note = note
        )
    )

    private fun UpdateEntryCommand.FromTotp.toModel() = authenticatorClient.newTotpEntryFromParams(
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
