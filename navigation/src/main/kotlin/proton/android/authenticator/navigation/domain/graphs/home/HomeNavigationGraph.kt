package proton.android.authenticator.navigation.domain.graphs.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.home.manual.ui.HomeManualScreen
import proton.android.authenticator.features.home.master.ui.HomeScreen
import proton.android.authenticator.features.home.scan.ui.HomeScanScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.settings.SettingsNavigationDestination

internal fun NavGraphBuilder.homeNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<HomeNavigationDestination>(startDestination = HomeMasterNavigationDestination) {
        composable<HomeMasterNavigationDestination> {
            HomeScreen(
                onEditEntryClick = { entryId ->
                    NavigationCommand.NavigateTo(
                        destination = HomeManualNavigationDestination(
                            entryId = entryId
                        )
                    ).also(onNavigate)
                },
                onSettingsClick = {
                    NavigationCommand.NavigateTo(
                        destination = SettingsNavigationDestination
                    ).also(onNavigate)
                },
                onNewEntryClick = {
                    NavigationCommand.NavigateTo(
                        destination = HomeScanNavigationDestination
                    ).also(onNavigate)
                }
            )
        }

        composable<HomeManualNavigationDestination> {
            HomeManualScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onEntryCreated = {
                    NavigationCommand.PopupTo(
                        destination = HomeNavigationDestination,
                        inclusive = true
                    ).also(onNavigate)
                },
                onEntryUpdated = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }

        composable<HomeScanNavigationDestination> {
            HomeScanScreen(
                onCloseClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onEnterManuallyClick = {
                    NavigationCommand.NavigateTo(
                        destination = HomeManualNavigationDestination(entryId = null)
                    ).also(onNavigate)
                }
            )
        }
    }
}
