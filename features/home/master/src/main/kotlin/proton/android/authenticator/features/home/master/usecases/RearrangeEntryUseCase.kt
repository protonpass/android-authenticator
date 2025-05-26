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

package proton.android.authenticator.features.home.master.usecases

import proton.android.authenticator.business.entries.application.shared.constants.EntryConstants
import proton.android.authenticator.business.entries.application.update.UpdateEntryCommand
import proton.android.authenticator.business.entries.application.update.UpdateEntryReason
import proton.android.authenticator.business.entries.domain.EntryType
import proton.android.authenticator.features.home.master.presentation.HomeMasterEntryModel
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandBus
import javax.inject.Inject

internal class RearrangeEntryUseCase @Inject constructor(private val commandBus: CommandBus) {

    internal suspend operator fun invoke(
        fromEntryId: String,
        fromEntryIndex: Int,
        toEntryId: String,
        toEntryIndex: Int,
        entryModelsMap: Map<String, HomeMasterEntryModel>
    ): Answer<Unit, UpdateEntryReason> {
        val fromEntryModel = entryModelsMap[fromEntryId] ?: return Answer.Failure(
            reason = UpdateEntryReason.EntryNotFound
        )

        val toEntryModel = entryModelsMap[toEntryId] ?: return Answer.Failure(
            reason = UpdateEntryReason.EntryNotFound
        )

        return getEntryNewPosition(fromEntryIndex, toEntryIndex, toEntryModel, entryModelsMap).let { newEntryPosition ->
            when (fromEntryModel.type) {
                EntryType.TOTP -> UpdateEntryCommand.FromTotp(
                    id = fromEntryModel.id,
                    name = fromEntryModel.name,
                    secret = fromEntryModel.secret,
                    issuer = fromEntryModel.issuer,
                    period = fromEntryModel.timeInterval,
                    digits = fromEntryModel.digits,
                    algorithm = fromEntryModel.algorithm,
                    position = newEntryPosition
                )

                EntryType.STEAM -> UpdateEntryCommand.FromSteam(
                    id = fromEntryModel.id,
                    name = fromEntryModel.name,
                    secret = fromEntryModel.secret,
                    position = newEntryPosition
                )
            }
        }.let { command -> commandBus.dispatch(command) }
    }

    private fun getEntryNewPosition(
        fromEntryIndex: Int,
        toEntryIndex: Int,
        toEntry: HomeMasterEntryModel,
        entryModelsMap: Map<String, HomeMasterEntryModel>
    ) = when (toEntryIndex) {
        0 -> {
            toEntry.position.minus(EntryConstants.POSITION_INCREMENT)
        }

        entryModelsMap.size.minus(1) -> {
            toEntry.position.plus(EntryConstants.POSITION_INCREMENT)
        }

        else -> {
            if (fromEntryIndex < toEntryIndex) {
                toEntryIndex.plus(1)
            } else {
                toEntryIndex.minus(1)
            }.let { entryIndex ->
                entryModelsMap.values
                    .toList()[entryIndex]
                    .position
                    .plus(toEntry.position)
                    .div(2)
            }
        }
    }
}
