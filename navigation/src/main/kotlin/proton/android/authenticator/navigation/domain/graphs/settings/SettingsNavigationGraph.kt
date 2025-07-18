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

package proton.android.authenticator.navigation.domain.graphs.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import proton.android.authenticator.features.exports.completion.ui.ExportsCompletionScreen
import proton.android.authenticator.features.exports.errors.ui.ExportsErrorsScreen
import proton.android.authenticator.features.imports.completion.ui.ImportsCompletionScreen
import proton.android.authenticator.features.imports.errors.ui.ImportsErrorScreen
import proton.android.authenticator.features.imports.onboarding.ui.ImportsOnboardingScreen
import proton.android.authenticator.features.imports.options.ui.ImportsOptionsScreen
import proton.android.authenticator.features.imports.passwords.ui.ImportsPasswordScreen
import proton.android.authenticator.features.logs.master.ui.LogsMasterScreen
import proton.android.authenticator.features.qa.ui.QaMenuMasterScreen
import proton.android.authenticator.features.settings.master.ui.SettingsMasterScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.graphs.backups.BackupsNavigationDestination
import proton.android.authenticator.navigation.domain.graphs.qa.QaMenuNavigationDestination
import proton.android.authenticator.navigation.domain.graphs.sync.SyncDisableNavigationDestination
import proton.android.authenticator.navigation.domain.graphs.sync.SyncNavigationDestination

@Suppress("LongMethod")
internal fun NavGraphBuilder.settingsNavigationGraph(
    snackbarHostState: SnackbarHostState,
    onNavigate: (NavigationCommand) -> Unit
) {
    navigation<SettingsNavigationDestination>(startDestination = SettingsMasterNavigationDestination) {
        composable<SettingsMasterNavigationDestination> {
            val context = LocalContext.current

            SettingsMasterScreen(
                snackbarHostState = snackbarHostState,
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onBackupsClick = {
                    NavigationCommand.NavigateTo(
                        destination = BackupsNavigationDestination
                    ).also(onNavigate)
                },
                onSyncEnabled = {
                    NavigationCommand.NavigateTo(
                        destination = SyncNavigationDestination
                    ).also(onNavigate)
                },
                onSyncDisabled = {
                    NavigationCommand.NavigateTo(
                        destination = SyncDisableNavigationDestination
                    ).also(onNavigate)
                },
                onExportCompleted = { exportedEntriesCount ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsExportCompletionNavigationDestination(
                            exportedEntriesCount = exportedEntriesCount
                        )
                    ).also(onNavigate)
                },
                onExportFailed = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsExportErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                },
                onImportClick = {
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportOptionsNavigationDestination
                    ).also(onNavigate)
                },
                onHowToClick = { howToUrl ->
                    NavigationCommand.NavigateToUrl(
                        url = howToUrl,
                        context = context
                    ).also(onNavigate)
                },
                onFeedbackClick = { feedbackUrl ->
                    NavigationCommand.NavigateToUrl(
                        url = feedbackUrl,
                        context = context
                    ).also(onNavigate)
                },
                onViewLogsClick = {
                    NavigationCommand.NavigateTo(
                        destination = SettingsViewLogsNavigationDestination
                    ).also(onNavigate)
                },
                onDiscoverAppClick = { appPackageName, appUrl ->
                    NavigationCommand.NavigateToPlayStore(
                        appPackageName = appPackageName,
                        context = context,
                        fallbackUrl = appUrl
                    ).also(onNavigate)
                },
                onVersionNameClick = {
                    NavigationCommand.NavigateTo(
                        destination = QaMenuNavigationDestination
                    ).also(onNavigate)
                }
            )
        }

        dialog<SettingsExportCompletionNavigationDestination> {
            ExportsCompletionScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = SettingsMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }

        dialog<SettingsExportErrorNavigationDestination> {
            ExportsErrorsScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = SettingsMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }

        dialog<SettingsImportCompletionNavigationDestination> {
            ImportsCompletionScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = SettingsMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }

        dialog<SettingsImportErrorNavigationDestination> {
            ImportsErrorScreen(
                onDismissed = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }

        composable<SettingsImportOptionsNavigationDestination> {
            ImportsOptionsScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onImportTypeSelected = { importType ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportOnboardingNavigationDestination(
                            importType = importType
                        )
                    ).also(onNavigate)
                }
            )
        }

        composable<SettingsImportOnboardingNavigationDestination> {
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
                        destination = SettingsImportPasswordNavigationDestination(
                            uri = uri,
                            importType = importType
                        )
                    ).also(onNavigate)
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        )
                    ).also(onNavigate)
                },
                onError = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                }
            )
        }

        composable<SettingsImportPasswordNavigationDestination> {
            ImportsPasswordScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                },
                onCompleted = { importedEntriesCount ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportCompletionNavigationDestination(
                            importedEntriesCount = importedEntriesCount
                        )
                    ).also(onNavigate)
                },
                onFailed = { errorReason ->
                    NavigationCommand.NavigateTo(
                        destination = SettingsImportErrorNavigationDestination(
                            errorReason = errorReason
                        )
                    ).also(onNavigate)
                }
            )
        }

        composable<SettingsViewLogsNavigationDestination> {
            LogsMasterScreen(
                onNavigationClick = {
                    onNavigate(NavigationCommand.NavigateUp)
                }
            )
        }

        composable<QaMenuNavigationDestination> {
            QaMenuMasterScreen(
                onDismissed = {
                    NavigationCommand.PopupTo(
                        destination = SettingsMasterNavigationDestination,
                        inclusive = false
                    ).also(onNavigate)
                }
            )
        }
    }
}
