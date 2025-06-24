package proton.android.authenticator.navigation.domain.graphs.onboarding

import kotlinx.serialization.Serializable
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination

@Serializable
internal data class OnboardingImportOnboardingNavigationDestination(
    internal val importType: Int
) : NavigationDestination
