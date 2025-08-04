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

package proton.android.authenticator.navigation.domain.graphs.exports

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.exports.completion.ui.ExportsCompletionScreen
import proton.android.authenticator.features.exports.errors.ui.ExportsErrorsScreen
import proton.android.authenticator.features.exports.passwords.ui.ExportsPasswordsScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.settings.SettingsMasterNavigationDestination

internal fun NavGraphBuilder.exportsNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<ExportsNavigationDestination>(startDestination = ExportsPasswordsNavigationDestination) {
        composable<ExportsPasswordsNavigationDestination> {
            ExportsPasswordsScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onExportCompleted = { exportedEntriesCount ->
                    NavigationCommand.NavigateToWithPopup(
                        destination = ExportsCompletionNavigationDestination(
                            exportedEntriesCount = exportedEntriesCount
                        ),
                        popDestination = SettingsMasterNavigationDestination
                    ).also(onNavigate)
                },
                onExportFailed = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = ExportsErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                }
            )
        }

        dialog<ExportsCompletionNavigationDestination> {
            ExportsCompletionScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = SettingsMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }

        dialog<ExportsErrorNavigationDestination> {
            ExportsErrorsScreen(
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }
    }
}
