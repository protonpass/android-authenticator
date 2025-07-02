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

import proton.android.authenticator.commonrust.AuthenticatorException
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class CreateEntryCommandHandler @Inject constructor(
    private val authenticatorClient: AuthenticatorMobileClientInterface,
    private val creator: EntryCreator
) : CommandHandler<CreateEntryCommand, Unit, CreateEntryReason> {

    override suspend fun handle(command: CreateEntryCommand): Answer<Unit, CreateEntryReason> = try {
        command.toModel(authenticatorClient)
            .let { model -> creator.create(model = model) }
            .let(Answer<Unit, CreateEntryReason>::Success)
    } catch (_: AuthenticatorException.InvalidName) {
        Answer.Failure(reason = CreateEntryReason.InvalidEntryTitle)
    } catch (_: AuthenticatorException.InvalidSecret) {
        Answer.Failure(reason = CreateEntryReason.InvalidEntrySecret)
    } catch (_: AuthenticatorException) {
        Answer.Failure(reason = CreateEntryReason.Unknown)
    } catch (_: IllegalStateException) {
        Answer.Failure(reason = CreateEntryReason.CannotSaveEntry)
    }

}
