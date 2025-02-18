package proton.android.authenticator.navigation.domain.navigators

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface NavigationNavigator {

    @Composable
    fun NavGraphs(navController: NavHostController)

}
