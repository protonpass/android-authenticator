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

package proton.android.authenticator.features.shared.users.usecases

import kotlinx.coroutines.flow.first
import proton.android.authenticator.business.users.application.delete.DeleteUserCommand
import proton.android.authenticator.business.users.application.delete.DeleteUserReason
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val commandBus: CommandBus,
    private val observeUserUseCase: ObserveUserUseCase
) {

    suspend operator fun invoke(): Answer<Unit, DeleteUserReason> = observeUserUseCase()
        .first()
        ?.let { user -> DeleteUserCommand(userId = user.id) }
        ?.let { command -> commandBus.dispatch(command = command) }
        ?: Answer.Success(data = Unit)

}
