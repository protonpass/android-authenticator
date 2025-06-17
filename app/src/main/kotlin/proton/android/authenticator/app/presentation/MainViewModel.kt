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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import proton.android.authenticator.R
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.biometrics.application.authentication.AuthenticateBiometricReason
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.common.AuthenticatorLogger
import proton.android.authenticator.features.shared.usecases.applock.ObserveAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.applock.UpdateAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import javax.inject.Inject

@[HiltViewModel OptIn(ExperimentalCoroutinesApi::class)]
internal class MainViewModel @Inject constructor(
    observeAppLockStateUseCase: ObserveAppLockStateUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateAppLockStateUseCase: UpdateAppLockStateUseCase,
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase
) : ViewModel() {

    internal val stateFlow: StateFlow<MainState> = combine(
        observeSettingsUseCase(),
        observeAppLockStateUseCase()
    ) { settings, appLockState ->
        MainState(
            settingsThemeType = settings.themeType,
            appLockState = appLockState.takeIf {
                settings.appLockType == SettingsAppLockType.Biometric
            } ?: AppLockState.AUTHENTICATED
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainState(
            settingsThemeType = SettingsThemeType.System,
            appLockState = AppLockState.NOT_STARTED
        )
    )

    fun requestReauthentication(context: Context) {
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

    companion object {
        private const val TAG = "MainViewModel"
    }
}
