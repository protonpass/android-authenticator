package proton.android.authenticator.navigation.domain.graphs.settings

import kotlinx.serialization.Serializable
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

@Serializable
internal data class SettingsImportErrorNavigationDestination(
    internal val errorReason: Int
) : NavigationDestination
