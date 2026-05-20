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

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryEvent
import proton.android.authenticator.business.shared.telemetry.AuthenticatorTelemetryManager
import proton.android.authenticator.features.shared.app.usecases.GetBuildFlavorUseCase
import proton.android.authenticator.features.shared.entries.usecases.ObserveEntryModelsUseCase
import proton.android.authenticator.features.shared.usecases.applock.ObserveAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.featureflag.FeatureFlag
import proton.android.authenticator.features.shared.usecases.featureflag.ObserveFeatureFlagUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.navigation.domain.flows.NavigationFlow
import proton.android.authenticator.navigation.domain.flows.RateAppReviewReason
import proton.android.authenticator.shared.common.domain.builds.BuildFlavorType
import proton.android.authenticator.shared.common.domain.providers.TimeProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    observeEntryModelsUseCase: ObserveEntryModelsUseCase,
    private val getBuildFlavorUseCase: GetBuildFlavorUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val accountManager: AccountManager,
    private val authOrchestrator: AuthOrchestrator,
    private val timeProvider: TimeProvider,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val observeAppLockStateUseCase: ObserveAppLockStateUseCase,
    observeFeatureFlagUseCase: ObserveFeatureFlagUseCase,
    private val telemetryManager: AuthenticatorTelemetryManager
) : ViewModel() {

    private var reviewAlreadyAsked = false

    private val isNewReviewTriggersEnabled: StateFlow<Boolean> =
        observeFeatureFlagUseCase(FeatureFlag.NewReviewTriggers).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FeatureFlag.NewReviewTriggers.isEnabledDefault
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val settingsStateFlow: StateFlow<SettingsState> = combine(
        observeSettingsUseCase().map { it.appLockType },
        observeAppLockStateUseCase()
    ) { appLockType, appLockState ->
        SettingsState(
            appLockType = appLockType,
            appLockState = if (appLockType == SettingsAppLockType.Biometric) appLockState
            else AppLockState.AuthNotRequired
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SettingsState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val stateFlow: StateFlow<MainState> = combine(
        observeSettingsUseCase(),
        observeEntryModelsUseCase(),
        MainState::Ready
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = MainState.Loading
    )

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

    internal fun setInstallationTimeIfFirstRun(state: MainState.Ready) {
        viewModelScope.launch {
            if (state.isFirstRun) {
                observeSettingsUseCase()
                    .first()
                    .copy(
                        isFirstRun = false,
                        installationTime = timeProvider.currentMillis()
                    )
                    .let { updatedSettings -> updateSettingsUseCase(settings = updatedSettings) }
            }
        }
    }

    internal fun askForReviewIfApplicable(
        state: MainState.Ready,
        reason: RateAppReviewReason,
        displayRating: () -> Unit
    ) {
        if (shouldRequestReview(state)) {
            viewModelScope.launch {
                telemetryManager.sendEvent(
                    AuthenticatorTelemetryEvent.RateAppRequested(
                        source = reason.toTelemetrySource()
                    )
                )
            }

            displayRating()

            reviewAlreadyAsked = true
        }
    }

    private fun shouldRequestReview(state: MainState.Ready): Boolean {
        if (reviewAlreadyAsked) return false

        if (!isReviewSupportedFlavor()) return false

        if (isNewReviewTriggersEnabled.value) {
            return state.numberOfEntries >= MIN_NUM_OF_ENTRIES_NEW_TRIGGERS
        }

        val installationTime = state.installationTime ?: return false
        val sevenDaysInMillis = TimeUnit.DAYS.toMillis(7)
        val distanceInMillis = timeProvider.currentMillis().minus(installationTime)
        return state.numberOfEntries >= MIN_NUM_OF_ENTRIES && distanceInMillis >= sevenDaysInMillis
    }

    private fun isReviewSupportedFlavor(): Boolean = when (getBuildFlavorUseCase().type) {
        BuildFlavorType.Fdroid -> false
        BuildFlavorType.Alpha,
        BuildFlavorType.Dev,
        BuildFlavorType.PlayStore -> true
    }

    private fun RateAppReviewReason.toTelemetrySource(): AuthenticatorTelemetryEvent.RateAppRequested.Source =
        when (this) {
            RateAppReviewReason.CreateItems -> AuthenticatorTelemetryEvent.RateAppRequested.Source.CreateItems
            RateAppReviewReason.GoToSettings -> AuthenticatorTelemetryEvent.RateAppRequested.Source.GoToSettings
            RateAppReviewReason.MoveItem -> AuthenticatorTelemetryEvent.RateAppRequested.Source.MoveItem
        }


    private companion object {

        private const val MIN_NUM_OF_ENTRIES = 4

        private const val MIN_NUM_OF_ENTRIES_NEW_TRIGGERS = 1

    }

}
