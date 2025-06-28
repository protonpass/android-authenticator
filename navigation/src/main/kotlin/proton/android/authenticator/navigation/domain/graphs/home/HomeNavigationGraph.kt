package proton.android.authenticator.navigation.domain.graphs.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.home.manual.ui.HomeManualScreen
import proton.android.authenticator.features.home.master.ui.HomeScreen
import proton.android.authenticator.features.home.scan.ui.HomeScanScreen
import proton.android.authenticator.features.imports.completion.ui.ImportsCompletionScreen
import proton.android.authenticator.features.imports.errors.ui.ImportsErrorScreen
import proton.android.authenticator.features.imports.onboarding.ui.ImportsOnboardingScreen
import proton.android.authenticator.features.imports.options.ui.ImportsOptionsScreen
import proton.android.authenticator.features.imports.passwords.ui.ImportsPasswordScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.settings.SettingsNavigationDestination

@Suppress("LongMethod")
internal fun NavGraphBuilder.homeNavigationGraph(
    snackbarHostState: SnackbarHostState,
    onEntryCreated: () -> Unit,
    onEntriesRearranged: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigate: (NavigationCommand) -> Unit
) {
    navigation<HomeNavigationDestination>(startDestination = HomeMasterNavigationDestination) {
        composable<HomeMasterNavigationDestination> {
            HomeScreen(
                snackbarHostState = snackbarHostState,
                onEditEntryClick = { entryId ->
                    NavigationCommand.NavigateTo(
                        destination = HomeManualNavigationDestination(
                            entryId = entryId
                        )
                    ).also(onNavigate)
                },
                onSettingsClick = {
                    onOpenSettings()
                    NavigationCommand.NavigateTo(
                        destination = SettingsNavigationDestination
                    ).also(onNavigate)
                },
                onNewEntryClick = {
                    NavigationCommand.NavigateTo(
                        destination = HomeScanNavigationDestination
                    ).also(onNavigate)
                },
                onImportEntriesClick = {
                    NavigationCommand.NavigateTo(
                        destination = HomeImportNavigationDestination
                    ).also(onNavigate)
                },
                onEntriesRearranged = onEntriesRearranged
            )
        }

        composable<HomeManualNavigationDestination> {
            HomeManualScreen(
                snackbarHostState = snackbarHostState,
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onEntryCreated = {
                    onEntryCreated()
                    NavigationCommand.PopupTo(
                        destination = HomeMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                },
                onEntryUpdated = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }

        composable<HomeScanNavigationDestination> {
            val context = LocalContext.current

            HomeScanScreen(
                snackbarHostState = snackbarHostState,
                onCloseClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onManualEntryClick = {
                    NavigationCommand.NavigateToWithPopup(
                        destination = HomeManualNavigationDestination(entryId = null),
                        popDestination = HomeMasterNavigationDestination
                    ).also(onNavigate)
                },
                onEntryCreated = {
                    onEntryCreated()
                    NavigationCommand.PopupTo(
                        destination = HomeMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                },
                onAppSettingsClick = {
                    onNavigate(NavigationCommand.NavigateToAppSettings(context))
                }
            )
        }

        composable<HomeImportNavigationDestination> {
            ImportsOptionsScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onImportTypeSelected = { importType ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportOnboardingNavigationDestination(
                            importType = importType
                        )
                    ).also(onNavigate)
                }
            )
        }

        composable<HomeImportOnboardingNavigationDestination> {
            val context = LocalContext.current

            ImportsOnboardingScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onHelpClick = { url ->
                    NavigationCommand.NavigateToUrl(
                        url = url,
                        context = context
                    ).also(onNavigate)
                },
                onPasswordRequired = { uri, importType ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportPasswordNavigationDestination(
                            uri = uri,
                            importType = importType
                        )
                    ).also(onNavigate)
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        )
                    ).also(onNavigate)
                },
                onError = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                }
            )
        }

        composable<HomeImportPasswordNavigationDestination> {
            ImportsPasswordScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        )
                    ).also(onNavigate)
                },
                onFailed = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = HomeImportErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                }
            )
        }

        dialog<HomeImportCompletionNavigationDestination> {
            ImportsCompletionScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = HomeMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }

        dialog<HomeImportErrorNavigationDestination> {
            ImportsErrorScreen(
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }
    }
}
