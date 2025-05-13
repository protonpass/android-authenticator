package proton.android.authenticator.navigation.commands

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
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

            is NavigationCommand.NavigateToPlayStore -> {
                val playStoreUri = "$PLAY_STORE_APP_URI${command.appPackageName}".toUri()
                val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri).apply {
                    setPackage(PLAY_STORE_VENDOR_PACKAGE)
                }

                try {
                    command.context.startActivity(playStoreIntent)
                } catch (_: ActivityNotFoundException) {
                    NavigationCommand.NavigateToUrl(
                        url = command.fallbackUrl ?: "$PLAY_STORE_WEB_URI${command.appPackageName}",
                        context = command.context
                    ).also { urlCommand -> handle(urlCommand, navController) }
                }
            }

            is NavigationCommand.NavigateToUrl -> {
                command.url.toUri()
                    .let { uri -> Intent(Intent.ACTION_VIEW, uri) }
                    .also { intent ->
                        try {
                            command.context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            return
                        }
                    }
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

    private companion object {

        private const val PLAY_STORE_APP_URI = "market://details?id="

        private const val PLAY_STORE_WEB_URI = "https://play.google.com/store/apps/details?id="

        private const val PLAY_STORE_VENDOR_PACKAGE = "com.android.vending"

    }

}
