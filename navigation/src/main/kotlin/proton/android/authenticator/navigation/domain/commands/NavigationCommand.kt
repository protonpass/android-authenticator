package proton.android.authenticator.navigation.domain.commands

import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

internal sealed interface NavigationCommand {

    data class NavigateTo(internal val destination: NavigationDestination) : NavigationCommand

    data object NavigateUp : NavigationCommand

    data class PopupTo(
        internal val destination: NavigationDestination,
        internal val inclusive: Boolean
    ) : NavigationCommand

}
