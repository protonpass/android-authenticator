package proton.android.authenticator.navigation.domain.graphs.onboarding

import kotlinx.serialization.Serializable
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

@Serializable
internal data class OnboardingImportErrorNavigationDestination(
    internal val errorReason: Int
) : NavigationDestination
