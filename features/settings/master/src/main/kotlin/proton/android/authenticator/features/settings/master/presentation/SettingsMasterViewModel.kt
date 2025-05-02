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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.business.settings.domain.SettingsDigitType
import proton.android.authenticator.business.settings.domain.SettingsSearchBarType
import proton.android.authenticator.business.settings.domain.SettingsThemeType
import proton.android.authenticator.features.settings.master.usecases.ObserveUninstalledProtonApps
import proton.android.authenticator.features.settings.master.usecases.UpdateSettingsUseCase
import proton.android.authenticator.features.shared.usecases.ObserveSettingsUseCase
import javax.inject.Inject

@HiltViewModel
internal class SettingsMasterViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeUninstalledProtonApps: ObserveUninstalledProtonApps,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val settingsModel: SettingsMasterSettingsModel
        get() = stateFlow.value.settingsModel

    internal val stateFlow: StateFlow<SettingsMasterState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        SettingsMasterState.create(
            settingsFlow = observeSettingsUseCase(),
            uninstalledProtonAppsFlow = observeUninstalledProtonApps()
        )
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

    internal fun onUpdateAppLockType(newAppLockType: SettingsAppLockType) {
        settingsModel.copy(appLockType = newAppLockType)
            .also(::updateSettings)
    }

    internal fun onUpdateIsTapToRevealEnabled(newIsTapToRevealEnabled: Boolean) {
        settingsModel.copy(isHideCodesEnabled = newIsTapToRevealEnabled)
            .also(::updateSettings)
    }

    internal fun onUpdateThemeType(newThemeType: SettingsThemeType) {
        settingsModel.copy(themeType = newThemeType)
            .also(::updateSettings)
    }

    internal fun onUpdateSearchBarType(newSearchBarType: SettingsSearchBarType) {
        settingsModel.copy(searchBarType = newSearchBarType)
            .also(::updateSettings)
    }

    internal fun onUpdateDigitType(newDigitType: SettingsDigitType) {
        settingsModel.copy(digitType = newDigitType)
            .also(::updateSettings)
    }

    internal fun onUpdateIsCodeChangeAnimationEnabled(newIsCodeChangeAnimationEnabled: Boolean) {
        settingsModel.copy(isCodeChangeAnimationEnabled = newIsCodeChangeAnimationEnabled)
            .also(::updateSettings)
    }

    private fun updateSettings(newSettingsModel: SettingsMasterSettingsModel) {
        viewModelScope.launch {
            updateSettingsUseCase(newSettingsModel)
        }
    }

}
