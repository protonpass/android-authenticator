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

package proton.android.authenticator.features.settings.master.presentation

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.applock.domain.AppLockState
import proton.android.authenticator.business.biometrics.application.authentication.AuthenticateBiometricReason
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.features.settings.master.usecases.ExportEntriesUseCase
import proton.android.authenticator.features.settings.master.usecases.ObserveUninstalledProtonApps
import proton.android.authenticator.features.shared.app.usecases.GetAppVersionNameUseCase
import proton.android.authenticator.features.shared.app.usecases.GetBuildFlavorUseCase
import proton.android.authenticator.features.shared.usecases.applock.UpdateAppLockStateUseCase
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.usecases.snackbars.DispatchSnackbarEventUseCase
import proton.android.authenticator.features.shared.users.usecases.ObserveIsUserAuthenticatedUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import proton.android.authenticator.shared.common.domain.models.SnackbarEvent
import javax.inject.Inject

@HiltViewModel
internal class SettingsMasterViewModel @Inject constructor(
    getBuildFlavorUseCase: GetBuildFlavorUseCase,
    getAppVersionNameUseCase: GetAppVersionNameUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    observeUninstalledProtonApps: ObserveUninstalledProtonApps,
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val updateAppLockStateUseCase: UpdateAppLockStateUseCase,
    private val exportEntriesUseCase: ExportEntriesUseCase,
    private val dispatchSnackbarEventUseCase: DispatchSnackbarEventUseCase,
    private val observeIsUserAuthenticatedUseCase: ObserveIsUserAuthenticatedUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<SettingsMasterEvent>(value = SettingsMasterEvent.Idle)

    internal val stateFlow: StateFlow<SettingsMasterState> = combine(
        eventFlow,
        getBuildFlavorUseCase().let(::flowOf),
        getAppVersionNameUseCase().let(::flowOf),
        observeSettingsUseCase(),
        observeUninstalledProtonApps(),
        SettingsMasterState::Ready
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SettingsMasterState.Loading
    )

    internal fun onConsumeEvent(event: SettingsMasterEvent) {
        eventFlow.compareAndSet(expect = event, update = SettingsMasterEvent.Idle)
    }

    internal fun onExportEntries(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            exportEntriesUseCase(uri = uri).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        SettingsMasterEvent.OnEntriesExportError(errorReason = answer.reason.ordinal)
                    }

                    is Answer.Success -> {
                        SettingsMasterEvent.OnEntriesExportSuccess(exportedEntriesCount = answer.data)
                    }
                }.also { event -> eventFlow.update { event } }
            }
        }
    }

    internal fun onUpdateIsPassBannerDismissed(settingsModel: SettingsMasterSettingsModel) {
        settingsModel.copy(isPassBannerDismissed = true)
            .also(::updateSettings)
    }

    internal fun onUpdateIsSyncEnabled(settingsModel: SettingsMasterSettingsModel, newIsSyncEnabled: Boolean) {
        if (newIsSyncEnabled) {
            viewModelScope.launch {
                if (observeIsUserAuthenticatedUseCase().first()) {
                    settingsModel.copy(isSyncEnabled = true).also(::updateSettings)
                    return@launch
                }

                eventFlow.update { SettingsMasterEvent.OnSyncEnabled }
            }
        } else {
            eventFlow.update { SettingsMasterEvent.OnSyncDisabled }
        }
    }

    internal fun onUpdateAppLockType(
        settingsModel: SettingsMasterSettingsModel,
        newAppLockType: SettingsAppLockType,
        context: Context
    ) {
        if (settingsModel.appLockType == newAppLockType) return

        when (newAppLockType) {
            SettingsAppLockType.None -> Pair(
                first = R.string.settings_security_lock_disable_title,
                second = R.string.settings_security_lock_disable_subtitle
            )

            SettingsAppLockType.Biometric -> Pair(
                first = R.string.settings_security_lock_enable_title,
                second = R.string.settings_security_lock_enable_subtitle
            )
        }.also { (titleResId, subtitleResId) ->
            updateAppLockType(
                titleResId = titleResId,
                subtitleResId = subtitleResId,
                context = context,
                appLockType = newAppLockType,
                settingsModel = settingsModel
            )
        }
    }

    private fun updateAppLockType(
        @StringRes titleResId: Int,
        @StringRes subtitleResId: Int,
        context: Context,
        appLockType: SettingsAppLockType,
        settingsModel: SettingsMasterSettingsModel
    ) {
        viewModelScope.launch {
            authenticateBiometricUseCase(
                title = context.getString(titleResId),
                subtitle = context.getString(subtitleResId),
                context = context
            ).fold(
                onFailure = { reason ->
                    when (reason) {
                        AuthenticateBiometricReason.UserCanceled -> Unit

                        AuthenticateBiometricReason.Canceled,
                        AuthenticateBiometricReason.HardwareNotPresent,
                        AuthenticateBiometricReason.HardwareUnavailable,
                        AuthenticateBiometricReason.Lockout,
                        AuthenticateBiometricReason.LockoutPermanent,
                        AuthenticateBiometricReason.NegativeButton,
                        AuthenticateBiometricReason.NoBiometrics,
                        AuthenticateBiometricReason.NoDeviceCredential,
                        AuthenticateBiometricReason.NoSpace,
                        AuthenticateBiometricReason.NotEnrolled,
                        AuthenticateBiometricReason.Timeout,
                        AuthenticateBiometricReason.UnableToProcess,
                        AuthenticateBiometricReason.Unavailable,
                        AuthenticateBiometricReason.Unknown,
                        AuthenticateBiometricReason.Unsupported,
                        AuthenticateBiometricReason.Vendor,
                        AuthenticateBiometricReason.WrongContext -> {
                            dispatchSnackbarMessage(
                                messageResId = R.string.settings_snackbar_message_biometric_error
                            )
                        }
                    }
                },
                onSuccess = {
                    if (appLockType == SettingsAppLockType.Biometric) {
                        updateAppLockStateUseCase(state = AppLockState.AUTHENTICATED)
                    }

                    settingsModel.copy(appLockType = appLockType)
                        .also(::updateSettings)
                }
            )
        }
    }

    internal fun onUpdateIsTapToRevealEnabled(
        settingsModel: SettingsMasterSettingsModel,
        newIsTapToRevealEnabled: Boolean
    ) {
        settingsModel.copy(isHideCodesEnabled = newIsTapToRevealEnabled)
            .also(::updateSettings)
    }

    internal fun onUpdateThemeType(settingsModel: SettingsMasterSettingsModel, newThemeType: SettingsThemeType) {
        if (settingsModel.themeType == newThemeType) return

        settingsModel.copy(themeType = newThemeType)
            .also(::updateSettings)
    }

    internal fun onUpdateSearchBarType(
        settingsModel: SettingsMasterSettingsModel,
        newSearchBarType: SettingsSearchBarType
    ) {
        if (settingsModel.searchBarType == newSearchBarType) return

        settingsModel.copy(searchBarType = newSearchBarType)
            .also(::updateSettings)
    }

    internal fun onUpdateDigitType(settingsModel: SettingsMasterSettingsModel, newDigitType: SettingsDigitType) {
        if (settingsModel.digitType == newDigitType) return

        settingsModel.copy(digitType = newDigitType)
            .also(::updateSettings)
    }

    internal fun onUpdateIsCodeChangeAnimationEnabled(
        settingsModel: SettingsMasterSettingsModel,
        newIsCodeChangeAnimationEnabled: Boolean
    ) {
        settingsModel.copy(isCodeChangeAnimationEnabled = newIsCodeChangeAnimationEnabled)
            .also(::updateSettings)
    }

    private fun updateSettings(newSettingsModel: SettingsMasterSettingsModel) {
        viewModelScope.launch {
            updateSettingsUseCase(newSettingsModel.asSettings()).also { answer ->
                when (answer) {
                    is Answer.Failure -> dispatchSnackbarMessage(
                        messageResId = R.string.settings_snackbar_message_update_error
                    )

                    is Answer.Success -> Unit
                }
            }
        }
    }

    private suspend fun dispatchSnackbarMessage(@StringRes messageResId: Int) {
        SnackbarEvent(messageResId = messageResId).also { snackbarEvent ->
            dispatchSnackbarEventUseCase(snackbarEvent)
        }
    }

}
