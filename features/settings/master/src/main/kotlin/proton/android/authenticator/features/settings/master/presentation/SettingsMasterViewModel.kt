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
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.settings.master.R
import proton.android.authenticator.features.settings.master.usecases.ExportEntriesUseCase
import proton.android.authenticator.features.settings.master.usecases.ObserveUninstalledProtonApps
import proton.android.authenticator.features.shared.usecases.biometrics.AuthenticateBiometricUseCase
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import proton.android.authenticator.features.shared.usecases.settings.UpdateSettingsUseCase
import proton.android.authenticator.shared.common.domain.answers.Answer
import javax.inject.Inject

@HiltViewModel
internal class SettingsMasterViewModel @Inject constructor(
    private val authenticateBiometricUseCase: AuthenticateBiometricUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeUninstalledProtonApps: ObserveUninstalledProtonApps,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val exportEntriesUseCase: ExportEntriesUseCase
) : ViewModel() {

    private val eventFlow = MutableStateFlow<SettingsMasterEvent>(value = SettingsMasterEvent.Idle)

    private val settingsModel: SettingsMasterSettingsModel
        get() = stateFlow.value.settingsModel

    internal val stateFlow: StateFlow<SettingsMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        SettingsMasterState.create(
            settingsFlow = observeSettingsUseCase(),
            uninstalledProtonAppsFlow = observeUninstalledProtonApps(),
            eventFlow = eventFlow
        )
    }

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

    internal fun onUpdateIsPassBannerDismissed() {
        settingsModel.copy(isPassBannerDismissed = true)
            .also(::updateSettings)
    }

    internal fun onUpdateIsBackupEnabled(newIsBackupEnabled: Boolean) {
        settingsModel.copy(isBackupEnabled = newIsBackupEnabled)
            .also(::updateSettings)
    }

    internal fun onUpdateIsSyncEnabled(newIsSyncEnabled: Boolean) {
        settingsModel.copy(isSyncEnabled = newIsSyncEnabled)
            .also(::updateSettings)
    }

    internal fun onUpdateAppLockType(newAppLockType: SettingsAppLockType, context: Context) {
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
                appLockType = newAppLockType
            )
        }
    }

    private fun updateAppLockType(
        @StringRes titleResId: Int,
        @StringRes subtitleResId: Int,
        context: Context,
        appLockType: SettingsAppLockType
    ) {
        viewModelScope.launch {
            authenticateBiometricUseCase(
                title = context.getString(titleResId),
                subtitle = context.getString(subtitleResId),
                context = context
            ).also { answer ->
                when (answer) {
                    is Answer.Failure -> {
                        println("JIBIRI: failure -> ${answer.reason}")
                    }

                    is Answer.Success -> {
                        settingsModel.copy(appLockType = appLockType)
                            .also(::updateSettings)
                    }
                }
            }
        }
    }

    internal fun onUpdateIsTapToRevealEnabled(newIsTapToRevealEnabled: Boolean) {
        settingsModel.copy(isHideCodesEnabled = newIsTapToRevealEnabled)
            .also(::updateSettings)
    }

    internal fun onUpdateThemeType(newThemeType: SettingsThemeType) {
        if (settingsModel.themeType == newThemeType) return

        settingsModel.copy(themeType = newThemeType)
            .also(::updateSettings)
    }

    internal fun onUpdateSearchBarType(newSearchBarType: SettingsSearchBarType) {
        if (settingsModel.searchBarType == newSearchBarType) return

        settingsModel.copy(searchBarType = newSearchBarType)
            .also(::updateSettings)
    }

    internal fun onUpdateDigitType(newDigitType: SettingsDigitType) {
        if (settingsModel.digitType == newDigitType) return

        settingsModel.copy(digitType = newDigitType)
            .also(::updateSettings)
    }

    internal fun onUpdateIsCodeChangeAnimationEnabled(newIsCodeChangeAnimationEnabled: Boolean) {
        settingsModel.copy(isCodeChangeAnimationEnabled = newIsCodeChangeAnimationEnabled)
            .also(::updateSettings)
    }

    private fun updateSettings(newSettingsModel: SettingsMasterSettingsModel) {
        viewModelScope.launch {
            updateSettingsUseCase(newSettingsModel.asSettings())
        }
    }

}
