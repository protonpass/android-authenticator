/*
 * Copyright (c) 2025 Proton AG
 * This file is part of Proton AG and Proton Authenticator.
 *
 * Proton Authenticator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Authenticator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Authenticator.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.authenticator.navigation.domain.graphs.onboarding

import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.imports.completion.ui.ImportsCompletionScreen
import proton.android.authenticator.features.imports.options.ui.ImportsOptionsScreen
import proton.android.authenticator.features.onboarding.biometrics.ui.OnboardingBiometricsScreen
import proton.android.authenticator.features.onboarding.imports.ui.OnboardingImportScreen
import proton.android.authenticator.features.onboarding.master.ui.OnboardingMasterScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.home.HomeNavigationDestination

@Suppress("LongMethod")
internal fun NavGraphBuilder.onboardingNavigationGraph(onNavigate: (NavigationCommand) -> Unit) {
    navigation<OnboardingNavigationDestination>(startDestination = OnboardingMasterNavigationDestination) {
        composable<OnboardingMasterNavigationDestination> {
            OnboardingMasterScreen(
                onGetStartedClick = {
                    NavigationCommand.NavigateTo(
                        destination = OnboardingImportNavigationDestination
                    ).also(onNavigate)
                }
            )
        }

        composable<OnboardingImportNavigationDestination> {
            OnboardingImportScreen(
                onImportClick = {
                    NavigationCommand.NavigateTo(
                        destination = OnboardingImportOptionsNavigationDestination
                    ).also(onNavigate)
                },
                onSkipClick = {
                    NavigationCommand.NavigateTo(
                        destination = OnboardingBiometricsNavigationDestination
                    ).also(onNavigate)
                }
            )
        }

        dialog<OnboardingImportCompletionNavigationDestination> {
            ImportsCompletionScreen(
                onDismissed = {
                    NavigationCommand.NavigateTo(
                        destination = OnboardingBiometricsNavigationDestination
                    ).also(onNavigate)
                }
            )
        }

        bottomSheet<OnboardingImportOptionsNavigationDestination> {
            ImportsOptionsScreen(
                onPasswordRequired = { uri, importType ->
                    println("JIBIRI: onPasswordRequired() called with: uri = $uri")
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateToWithPopup(
                        destination = OnboardingImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        ),
                        popDestination = OnboardingImportNavigationDestination
                    ).also(onNavigate)
                },
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }

        composable<OnboardingBiometricsNavigationDestination> {
            OnboardingBiometricsScreen(
                onSkipClick = {
                    NavigationCommand.NavigateToWithPopup(
                        destination = HomeNavigationDestination,
                        popDestination = OnboardingNavigationDestination
                    ).also(onNavigate)
                }
            )
        }
    }
}
