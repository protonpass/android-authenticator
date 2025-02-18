package proton.android.authenticator.navigation.domain.graphs.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.home.master.ui.HomeMasterScreenRenderer
import proton.android.authenticator.navigation.domain.commands.NavigationCommand

internal fun NavGraphBuilder.homeNavigationGraph(
    onNavigate: (NavigationCommand) -> Unit,
) {
    navigation<HomeNavigationDestination>(startDestination = HomeMasterNavigationDestination) {
        composable<HomeMasterNavigationDestination> {
            HomeMasterScreenRenderer().Render()
        }
    }
}
