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

package proton.android.authenticator.business.entries.application.create

import kotlinx.datetime.Clock
import proton.android.authenticator.business.entries.domain.Entry
import proton.android.authenticator.commonrust.AuthenticatorEntrySteamCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorEntryTotpCreateParameters
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class CreateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val clock: Clock,
    private val creator: EntryCreator
) : CommandHandler<CreateEntryCommand> {

    override suspend fun handle(command: CreateEntryCommand) {
        when (command) {
            is CreateEntryCommand.FromSteam -> {
                authenticatorClient.newSteamEntryFromParams(
                    params = AuthenticatorEntrySteamCreateParameters(
                        name = command.name,
                        secret = command.secret,
                        note = command.note
                    )
                )
            }

            is CreateEntryCommand.FromTotp -> {
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

            is CreateEntryCommand.FromUri -> {
                authenticatorClient.entryFromUri(uri = command.uri)
            }
        }.let { model ->
            val currentTimestamp = clock.now().toEpochMilliseconds()

            Entry(
                id = 0, // will be replaced by id provided from Rust once it is implemented
                model = model,
                createdAt = currentTimestamp,
                modifiedAt = currentTimestamp
            )
        }.also { newEntry ->
            creator.create(newEntry)
        }
    }

}
