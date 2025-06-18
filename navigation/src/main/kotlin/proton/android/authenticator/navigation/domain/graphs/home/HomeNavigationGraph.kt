package proton.android.authenticator.navigation.domain.graphs.home

import androidx.compose.material.navigation.bottomSheet
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
import proton.android.authenticator.features.imports.options.ui.ImportsOptionsScreen
import proton.android.authenticator.features.imports.passwords.ui.ImportsPasswordScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.settings.SettingsNavigationDestination

@Suppress("LongMethod")
internal fun NavGraphBuilder.homeNavigationGraph(
    snackbarHostState: SnackbarHostState,
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
                }
            )
        }

        composable<HomeManualNavigationDestination> {
            HomeManualScreen(
                snackbarHostState = snackbarHostState,
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onEntryCreated = {
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

        bottomSheet<HomeImportNavigationDestination> {
            ImportsOptionsScreen(
                onPasswordRequired = { uri, importType ->
                    NavigationCommand.NavigateToWithPopup(
                        destination = HomeImportPasswordNavigationDestination(
                            uri = uri,
                            importType = importType
                        ),
                        popDestination = HomeMasterNavigationDestination
                    ).also(onNavigate)
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateToWithPopup(
                        destination = HomeImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        ),
                        popDestination = HomeMasterNavigationDestination
                    ).also(onNavigate)
                },
                onError = { errorReason ->
                    NavigationCommand.NavigateToWithPopup(
                        destination = HomeImportErrorNavigationDestination(
                            errorReason = errorReason
                        ),
                        popDestination = HomeMasterNavigationDestination
                    ).also(onNavigate)
                },
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
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
