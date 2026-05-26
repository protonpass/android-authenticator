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

package proton.android.authenticator.app.ui

import android.app.ComponentCaller
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import proton.android.authenticator.app.handler.RequestReviewHandler
import proton.android.authenticator.app.presentation.MainState
import proton.android.authenticator.app.presentation.MainViewModel
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.features.shared.usecases.applock.UpdateAppLockStateUseCase
import proton.android.authenticator.features.unlock.master.ui.UnlockMasterScreen
import proton.android.authenticator.navigation.domain.commands.NavigationCommand
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import proton.android.authenticator.shared.common.domain.builds.BuildFlavorType
import proton.android.authenticator.shared.common.domain.configs.AppConfig
import proton.android.authenticator.shared.ui.domain.theme.Theme
import proton.android.authenticator.shared.ui.domain.theme.isDarkTheme
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    internal lateinit var navigationNavigator: NavigationNavigator

    @Inject
    internal lateinit var updateAppLockStateUseCase: UpdateAppLockStateUseCase

    @Inject
    internal lateinit var requestReviewHandler: RequestReviewHandler

    @Inject
    internal lateinit var navigationCommandHandler: NavigationCommandHandler

    @Inject
    internal lateinit var appConfig: AppConfig

    @SuppressWarnings("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onRegisterOrchestrators(context = this)

        enableEdgeToEdge()

        setContent {
            val state by viewModel.stateFlow.collectAsStateWithLifecycle()

            when (val currentState = state) {
                MainState.Loading -> Unit
                is MainState.Ready -> {
                    val settingsState by viewModel.settingsStateFlow.collectAsStateWithLifecycle()
                    val context = LocalContext.current
                    val bottomSheetNavigator = rememberBottomSheetNavigator()
                    val navController = rememberNavController(bottomSheetNavigator)
                    setSecureMode(isSecure = currentState.isBiometricLockEnabled)

                    val isDarkTheme = isDarkTheme(currentState.themeType)
                    setStatusBarTheme(isDarkTheme)

                    navigationNavigator.NavGraphs(
                        isDarkTheme = isDarkTheme,
                        bottomSheetNavigator = bottomSheetNavigator,
                        navController = navController,
                        onAskForReview = { reason ->
                            viewModel.askForReviewIfApplicable(
                                state = currentState,
                                reason = reason,
                                displayRating = {
                                    showRateRequestDebugToast()
                                    requestReviewHandler.request(this@MainActivity)
                                }
                            )
                        },
                        onFinishLaunching = {
                            viewModel.setInstallationTimeIfFirstRun(currentState)
                        },
                        onLaunchNavigationFlow = viewModel::onLaunchNavigationFlow
                    )

                    // The lock screen must be displayed immediately,
                    // without relying on any asynchronous system
                    AnimatedVisibility(
                        visible = settingsState.appLockType == SettingsAppLockType.Biometric &&
                            settingsState.appLockState == AppLockState.AuthRequired,
                        enter = EnterTransition.None,
                        exit = fadeOut()
                    ) {
                        Theme(isDarkTheme = isDarkTheme) {
                            UnlockMasterScreen(
                                onUnlockClosed = {
                                    NavigationCommand.FinishAffinity(
                                        context = context
                                    ).also {
                                        navigationCommandHandler.handle(
                                            it,
                                            navController
                                        )
                                    }
                                },
                                onUnlockSucceeded = {}
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        lifecycleScope.launch {
            updateAppLockStateUseCase(state = AppLockState.AuthNotRequired)

            super.onActivityResult(requestCode, resultCode, data, caller)
        }
    }

    private fun showRateRequestDebugToast() {
        when (appConfig.buildFlavor.type) {
            BuildFlavorType.Alpha,
            BuildFlavorType.Dev ->
                Toast.makeText(this, "Launching rate request", Toast.LENGTH_SHORT).show()

            BuildFlavorType.Fdroid,
            BuildFlavorType.PlayStore -> Unit
        }
    }

    private fun setSecureMode(isSecure: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(!isSecure)
        }
    }

    private fun setStatusBarTheme(isDarkTheme: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView)
            .also { controller -> controller.isAppearanceLightStatusBars = !isDarkTheme }
    }
}
