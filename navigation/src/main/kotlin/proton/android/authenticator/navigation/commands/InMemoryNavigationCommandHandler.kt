package proton.android.authenticator.navigation.commands

import androidx.navigation.NavHostController
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import javax.inject.Inject

internal class InMemoryNavigationCommandHandler @Inject constructor() : NavigationCommandHandler {

    override fun handle(command: NavigationCommand, navController: NavHostController) {
        when (command) {
            is NavigationCommand.NavigateTo -> {
                navController.navigate(route = command.destination)
            }

            is NavigationCommand.NavigateToWithPopup -> {
                navController.navigate(route = command.destination) {
                    popUpTo(route = command.popDestination)
                }
            }

            NavigationCommand.NavigateUp -> {
                navController.navigateUp()
            }

            is NavigationCommand.PopupTo -> {
                navController.popBackStack(
                    route = command.destination,
                    inclusive = command.inclusive
                )
            }
        }
    }

}
