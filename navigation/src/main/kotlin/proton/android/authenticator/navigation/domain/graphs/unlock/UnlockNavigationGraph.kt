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

package proton.android.authenticator.navigation.domain.graphs.unlock

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.unlock.master.ui.UnlockMasterScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand

internal fun NavGraphBuilder.unlockNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<UnlockNavigationDestination>(startDestination = UnlockMasterNavigationDestination) {
        composable<UnlockMasterNavigationDestination> {
            val context = LocalContext.current

            UnlockMasterScreen(
                onUnlockClosed = {
                    NavigationCommand.FinishAffinity(
                        context = context
                    ).also(onNavigate)
                },
                onUnlockSucceeded = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }
    }
}
