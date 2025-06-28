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

package proton.android.authenticator.app.presentation

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.proton.core.accountmanager.domain.AccountManager
import me.proton.core.accountmanager.presentation.observe
import me.proton.core.accountmanager.presentation.onAccountCreateAddressFailed
import me.proton.core.accountmanager.presentation.onAccountCreateAddressNeeded
import me.proton.core.accountmanager.presentation.onAccountDeviceSecretNeeded
import me.proton.core.accountmanager.presentation.onAccountTwoPassModeFailed
import me.proton.core.accountmanager.presentation.onAccountTwoPassModeNeeded
import me.proton.core.accountmanager.presentation.onSessionSecondFactorNeeded
import me.proton.core.auth.presentation.AuthOrchestrator
import proton.android.authenticator.R
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.biometrics.application.authentication.AuthenticateBiometricReason
import proton.android.authenticator.business.settings.domain.Settings
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.shared.app.usecases.GetBuildFlavorUseCase
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.applock.ObserveAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.applock.UpdateAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.navigation.domain.flows.NavigationFlow
import proton.android.authenticator.shared.common.logger.AuthenticatorLogger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
internal class MainViewModel @Inject constructor(
    observeAppLockStateUseCase: ObserveAppLockStateUseCase,
    observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val getBuildFlavorUseCase: GetBuildFlavorUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase,
    private val accountManager: AccountManager,
    private val authOrchestrator: AuthOrchestrator,
    private val updateAppLockStateUseCase: UpdateAppLockStateUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    internal val stateFlow: StateFlow<MainState> = combine(
        observeSettingsUseCase(),
        observeEntryModelsUseCase(),
        observeAppLockStateUseCase()
    ) { settings, entries, appLockState ->
        MainState(
            settingsThemeType = settings.themeType,
            isFirstRun = settings.isFirstRun,
            installationTime = settings.installationTime,
            numberOfEntries = entries.size,
            appLockState = appLockState.takeIf {
                settings.appLockType == SettingsAppLockType.Biometric
            } ?: AppLockState.AUTHENTICATED
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainState(
            settingsThemeType = SettingsThemeType.System,
            isFirstRun = Settings.Default.isFirstRun,
            installationTime = Settings.Default.installationTime,
            numberOfEntries = DEFAULT_NUM_OF_ENTRIES,
            appLockState = AppLockState.NOT_STARTED
        )
    )

    val requestReview = MutableStateFlow<Unit?>(null)

    internal fun onRegisterOrchestrators(context: ComponentActivity) {
        authOrchestrator.register(context as ActivityResultCaller)

        accountManager.observe(context.lifecycle, Lifecycle.State.CREATED)
            .onAccountTwoPassModeFailed { account ->
                accountManager.disableAccount(userId = account.userId)
            }
            .onAccountCreateAddressFailed { account ->
                accountManager.disableAccount(userId = account.userId)
            }
            .onSessionSecondFactorNeeded { account ->
                authOrchestrator.startSecondFactorWorkflow(account = account)
            }
            .onAccountTwoPassModeNeeded { account ->
                authOrchestrator.startTwoPassModeWorkflow(account = account)
            }
            .onAccountCreateAddressNeeded { account ->
                authOrchestrator.startChooseAddressWorkflow(account = account)
            }
            .onAccountDeviceSecretNeeded { account ->
                authOrchestrator.startDeviceSecretWorkflow(account = account)
            }
    }

    internal fun onLaunchNavigationFlow(flow: NavigationFlow) {
        when (flow) {
            NavigationFlow.SignIn -> {
                authOrchestrator.startLoginWorkflow()
            }

            NavigationFlow.SignUp -> {
                authOrchestrator.startSignupWorkflow()
            }
        }
    }

    internal fun requestReauthentication(context: Context) {
        viewModelScope.launch {
            authenticateBiometricUseCase(
                title = context.getString(R.string.biometric_prompt_title),
                subtitle = context.getString(R.string.biometric_prompt_subtitle),
                context = context
            ).fold(
                onSuccess = {
                    updateAppLockStateUseCase(AppLockState.AUTHENTICATED)
                },
                onFailure = { reason: AuthenticateBiometricReason ->
                    AuthenticatorLogger.w(TAG, "Biometric authentication failed: ${reason.name}")
                    updateAppLockStateUseCase(AppLockState.LOCKED)
                }
            )
        }
    }

    internal fun setInstallationTimeIfFirstRun() {
        viewModelScope.launch {
            if (stateFlow.value.isFirstRun) {
                observeSettingsUseCase()
                    .first()
                    .copy(
                        isFirstRun = false,
                        installationTime = System.currentTimeMillis()
                    )
                    .let { updateSettingsUseCase(settings = it) }
            }
        }
    }

    internal fun askForReviewIfApplicable() {
        if (stateFlow.value.numberOfEntries < MIN_NUM_OF_ENTRIES) return

        val sevenDaysInMillis = TimeUnit.DAYS.toMillis(7)
        val distanceInMillis = System.currentTimeMillis() - stateFlow.value.installationTime
        if (distanceInMillis < sevenDaysInMillis) return

        val buildFlavor = getBuildFlavorUseCase()
        if (buildFlavor.isPlay() || buildFlavor.isDev() && !buildFlavor.isFdroid()) {
            requestReview.value = Unit
        }
    }

    private companion object {

        private const val TAG = "MainViewModel"
        private const val DEFAULT_NUM_OF_ENTRIES = 0
        private const val MIN_NUM_OF_ENTRIES = 4

    }

}
