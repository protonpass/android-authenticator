package proton.android.authenticator.navigation.commands

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.shared.common.logs.AuthenticatorLogger
import javax.inject.Inject

internal class InMemoryNavigationCommandHandler @Inject constructor() : NavigationCommandHandler {

    @Suppress("LongMethod")
    override fun handle(command: NavigationCommand, navController: NavHostController) {
        when (command) {
            is NavigationCommand.NavigateTo -> {
                navController.navigate(route = command.destination)
            }

            is NavigationCommand.NavigateToAppSettings -> {
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts(URI_SCHEME_PACKAGE, command.context.packageName, null)
                ).also { intent ->
                    try {
                        command.context.startActivity(intent)
                    } catch (error: ActivityNotFoundException) {
                        AuthenticatorLogger.w(TAG, "Cannot open app settings")
                        AuthenticatorLogger.w(TAG, error)
                    }
                }
            }

            is NavigationCommand.NavigateToPlayStore -> {
                val playStoreUri = "$PLAY_STORE_APP_URI${command.appPackageName}".toUri()
                val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri).apply {
                    setPackage(PLAY_STORE_VENDOR_PACKAGE)
                }

                try {
                    command.context.startActivity(playStoreIntent)
                } catch (error: ActivityNotFoundException) {
                    AuthenticatorLogger.w(TAG, "Cannot navigate to PlayStore: $playStoreUri")
                    AuthenticatorLogger.w(TAG, error)

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
                        } catch (error: ActivityNotFoundException) {
                            AuthenticatorLogger.w(TAG, "Cannot navigate to URL: ${command.url}")
                            AuthenticatorLogger.w(TAG, error)
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

            is NavigationCommand.ShareFileViaEmail -> {
                Bundle()
                    .apply {
                        putStringArray(Intent.EXTRA_EMAIL, arrayOf(command.emailReceiver))
                        putString(Intent.EXTRA_SUBJECT, command.emailSubject)
                        putParcelable(Intent.EXTRA_STREAM, command.fileUri)
                    }
                    .let { bundle ->
                        Intent(Intent.ACTION_SEND).apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtras(bundle)
                            type = command.mimeType
                        }
                    }
                    .let { intent -> Intent.createChooser(intent, command.chooserTitle) }
                    .also { chooserIntent ->
                        try {
                            command.context.startActivity(chooserIntent)
                        } catch (error: ActivityNotFoundException) {
                            AuthenticatorLogger.w(TAG, "Cannot share file via email")
                            AuthenticatorLogger.w(TAG, error)
                        }
                    }
            }
        }
    }

    private companion object {

        private const val TAG = "InMemoryNavigationCommandHandler"

        private const val PLAY_STORE_APP_URI = "market://details?id="

        private const val PLAY_STORE_WEB_URI = "https://play.google.com/store/apps/details?id="

        private const val PLAY_STORE_VENDOR_PACKAGE = "com.android.vending"

        private const val URI_SCHEME_PACKAGE = "package"

    }

}
