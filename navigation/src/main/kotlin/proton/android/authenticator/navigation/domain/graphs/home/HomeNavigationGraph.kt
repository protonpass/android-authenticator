package proton.android.authenticator.navigation.domain.graphs.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.home.manual.ui.HomeManualScreen
import proton.android.authenticator.features.home.master.ui.HomeScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.settings.SettingsNavigationDestination

internal fun NavGraphBuilder.homeNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<HomeNavigationDestination>(startDestination = HomeMasterNavigationDestination) {
        composable<HomeMasterNavigationDestination> {
            HomeScreen(
                onEditEntryClick = { entryId ->
                    NavigationCommand.NavigateTo(
                        destination = HomeDetailNavigationDestination(
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
                        destination = HomeManualNavigationDestination(entryId = null)
                    ).also(onNavigate)
                }
            )
        }

        composable<HomeManualNavigationDestination> {
            HomeManualScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }
    }
}
