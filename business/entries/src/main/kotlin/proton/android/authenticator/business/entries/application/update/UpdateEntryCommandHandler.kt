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
        command.toModel(authenticatorClient)
            .let { model ->
                updater.update(
                    id = command.id,
                    position = command.position,
                    model = model
                )
            }
            .let(Answer<Unit, UpdateEntryReason>::Success)
    } catch (_: AuthenticatorException.InvalidName) {
        Answer.Failure(reason = UpdateEntryReason.InvalidEntryTitle)
    } catch (_: AuthenticatorException.InvalidSecret) {
        Answer.Failure(reason = UpdateEntryReason.InvalidEntrySecret)
    } catch (_: AuthenticatorException) {
        Answer.Failure(reason = UpdateEntryReason.Unknown)
    } catch (_: IllegalStateException) {
        Answer.Failure(reason = UpdateEntryReason.EntryNotFound)
    }

}
