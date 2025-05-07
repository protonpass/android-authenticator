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

package proton.android.authenticator.navigation.domain.graphs.settings

import androidx.compose.material.navigation.bottomSheet
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.imports.options.ui.ImportsOptionsScreen
import proton.android.authenticator.features.settings.master.ui.SettingsScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand

internal fun NavGraphBuilder.settingsNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<SettingsNavigationDestination>(startDestination = SettingsMasterNavigationDestination) {
        composable<SettingsMasterNavigationDestination> {
            val context = LocalContext.current

            SettingsScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onImportClick = {
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportOptionsNavigationDestination
                    ).also(onNavigate)
                },
                onDiscoverAppClick = { appPackageName ->
                    NavigationCommand.NavigateToPlayStore(
                        appPackageName = appPackageName,
                        context = context
                    ).also(onNavigate)
                }
            )
        }

        bottomSheet<SettingsImportOptionsNavigationDestination> {
            ImportsOptionsScreen(
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }
    }
}
