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

package proton.android.authenticator.navigation.domain.graphs.backups

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.backups.master.ui.BackupsMasterScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand

internal fun NavGraphBuilder.backupsNavigationGraph(
    snackbarHostState: SnackbarHostState,
    onNavigate: (NavigationCommand) -> Unit
) {
    navigation<BackupsNavigationDestination>(startDestination = BackupsMasterNavigationDestination) {
        composable<BackupsMasterNavigationDestination> {
            BackupsMasterScreen(
                snackbarHostState = snackbarHostState,
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onBackupError = { errorReason ->
                    println("JIBIRI: errorReason = $errorReason")
                }
            )
        }
    }
}
