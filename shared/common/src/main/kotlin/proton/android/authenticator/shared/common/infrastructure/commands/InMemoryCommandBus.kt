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

package proton.android.authenticator.shared.common.infrastructure.commands

import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.answers.AnswerReason
import proton.android.authenticator.shared.common.domain.infrastructure.commands.Command
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class InMemoryCommandBus @Inject constructor(
    private val commandHandlers: Map<Class<out Command>, @JvmSuppressWildcards CommandHandler<*, *, *>>
) : CommandBus {

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T, A : AnswerReason> dispatch(command: Command): Answer<T, A> {
        return commandHandlers[command::class.java]
            ?.let { commandHandler -> (commandHandler as CommandHandler<Command, T, A>).handle(command) }
            ?: throw IllegalArgumentException("No command handler found for ${command::class.java.name}")
    }

}
