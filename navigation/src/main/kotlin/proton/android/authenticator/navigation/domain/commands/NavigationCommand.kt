package proton.android.authenticator.navigation.domain.commands

import android.content.Context
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

internal sealed interface NavigationCommand {

    data class NavigateTo(internal val destination: NavigationDestination) : NavigationCommand

    data class NavigateToPlayStore(
        internal val appPackageName: String,
        internal val context: Context
    ) : NavigationCommand

    data class NavigateToUrl(
        internal val url: String,
        internal val context: Context
    ) : NavigationCommand

    data class NavigateToWithPopup(
        internal val destination: NavigationDestination,
        internal val popDestination: NavigationDestination
    ) : NavigationCommand

    data object NavigateUp : NavigationCommand

    data class PopupTo(
        internal val destination: NavigationDestination,
        internal val inclusive: Boolean
    ) : NavigationCommand

}
