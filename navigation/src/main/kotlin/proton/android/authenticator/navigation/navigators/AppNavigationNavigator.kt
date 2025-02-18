package proton.android.authenticator.navigation.navigators

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination
import proton.android.authenticator.navigation.domain.graphs.home.homeNavigationGraph
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import javax.inject.Inject

internal class AppNavigationNavigator @Inject constructor(
    private val startDestination: NavigationDestination,
    private val navigationCommandHandler: NavigationCommandHandler,
) : NavigationNavigator {

    @Composable
    override fun NavGraphs(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            homeNavigationGraph { navCommand ->
                navigationCommandHandler.handle(navCommand, navController)
            }
        }
    }

}
