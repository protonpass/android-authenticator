package proton.android.authenticator.navigation.navigators

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination
import proton.android.authenticator.navigation.domain.graphs.home.homeNavigationGraph
import proton.android.authenticator.navigation.domain.graphs.onboarding.onboardingNavigationGraph
import proton.android.authenticator.navigation.domain.graphs.settings.settingsNavigationGraph
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import proton.android.authenticator.shared.ui.domain.theme.Theme
import javax.inject.Inject

internal class AppNavigationNavigator @Inject constructor(
    private val startDestination: NavigationDestination,
    private val navigationCommandHandler: NavigationCommandHandler
) : NavigationNavigator {

    @Composable
    override fun NavGraphs(navController: NavHostController) {
        Theme {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                homeNavigationGraph { navCommand ->
                    navigationCommandHandler.handle(navCommand, navController)
                }

                onboardingNavigationGraph { navCommand ->
                    navigationCommandHandler.handle(navCommand, navController)
                }

                settingsNavigationGraph { navCommand ->
                    navigationCommandHandler.handle(navCommand, navController)
                }
            }
        }
    }

}
