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

package proton.android.authenticator.features.home.manual.usecases

import proton.android.authenticator.business.entries.application.create.CreateEntryCommand
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.manual.presentation.HomeManualFormModel
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

internal class CreateEntryUseCase @Inject constructor(private val commandBus: CommandBus) {

    internal suspend operator fun invoke(formModel: HomeManualFormModel) {
        when (formModel.type) {
            EntryType.TOTP -> CreateEntryCommand.FromTotp(
                name = formModel.title,
                secret = formModel.secret,
                issuer = formModel.issuer,
                period = formModel.timeInterval,
                digits = formModel.digits,
                algorithm = formModel.algorithm
            )

            EntryType.STEAM -> CreateEntryCommand.FromSteam(
                name = formModel.title,
                secret = formModel.secret
            )
        }.also { command ->
            commandBus.dispatch(command)
        }
    }

}
