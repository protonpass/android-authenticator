package proton.android.authenticator.navigation.domain.graphs.home

import kotlinx.serialization.Serializable
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

@Serializable
internal data class HomeDetailNavigationDestination(
    internal val entryId: String
) : NavigationDestination
