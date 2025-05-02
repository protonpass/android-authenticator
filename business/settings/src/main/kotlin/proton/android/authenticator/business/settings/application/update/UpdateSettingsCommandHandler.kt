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

package proton.android.authenticator.business.settings.application.update

import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import javax.inject.Inject

internal class UpdateSettingsCommandHandler @Inject constructor(
    private val updater: SettingsUpdater
) : CommandHandler<UpdateSettingsCommand> {

    override suspend fun handle(command: UpdateSettingsCommand) {
        Settings(
            isBackupEnabled = command.isBackupEnabled,
            isSyncEnabled = command.isSyncEnabled,
            appLockType = command.appLockType,
            isHideCodesEnabled = command.isTapToRevealEnabled,
            themeType = command.themeType,
            searchBarType = command.searchBarType,
            digitType = command.digitType,
            isCodeChangeAnimationEnabled = command.isCodeChangeAnimationEnabled,
            isPassBannerDismissed = command.isPassBannerDismissed
        ).also { settings ->
            updater.update(settings)
        }
    }

}
