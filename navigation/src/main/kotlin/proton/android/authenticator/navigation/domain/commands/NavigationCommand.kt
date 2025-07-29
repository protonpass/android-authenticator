package proton.android.authenticator.navigation.domain.commands

import android.content.Context
import android.net.Uri
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination
import proton.android.authenticator.shared.common.domain.models.MimeType
import proton.android.authenticator.shared.ui.R

internal sealed interface NavigationCommand {

    data class FinishAffinity(internal val context: Context) : NavigationCommand

    data class NavigateTo(internal val destination: NavigationDestination) : NavigationCommand

    data class NavigateToAppSettings(internal val context: Context) : NavigationCommand

    data class NavigateToPlayStore(
        internal val appPackageName: String,
        internal val context: Context,
        internal val fallbackUrl: String? = null
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

    data class ShareFileViaEmail(
        internal val fileUri: Uri,
        internal val context: Context
    ) : NavigationCommand {

        internal val chooserTitle: String = "${context.getString(R.string.action_share)} logs"

        internal val emailReceiver: String = "pass@protonme.zendesk.com"

        internal val emailSubject: String = "Proton Authenticator: Share Logs"

        internal val mimeType: String = MimeType.Text.value

    }

}
