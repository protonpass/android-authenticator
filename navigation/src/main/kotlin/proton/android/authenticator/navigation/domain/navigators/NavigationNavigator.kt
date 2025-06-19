package proton.android.authenticator.navigation.domain.navigators

import androidx.compose.runtime.Composable
import proton.android.authenticator.navigation.domain.flows.NavigationFlow

interface NavigationNavigator {

    @Composable
    fun NavGraphs(isDarkTheme: Boolean, onLaunchNavigationFlow: (NavigationFlow) -> Unit)

}
