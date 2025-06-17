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

package proton.android.authenticator.app.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import proton.android.authenticator.business.settings.domain.SettingsAppLockType
import proton.android.authenticator.features.shared.usecases.settings.ObserveSettingsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase
) {
    private val mutableAuthState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.LOCKED)

    val authStateFlow: Flow<AuthState> = combine(
        observeSettingsUseCase(),
        mutableAuthState
    ) { settings, authState ->
        when {
            settings.appLockType != SettingsAppLockType.Biometric -> AuthState.AUTHENTICATED
            else -> authState
        }
    }

    fun lock() {
        mutableAuthState.update { AuthState.LOCKED }
    }

    fun requestReauthentication() {
        mutableAuthState.update { AuthState.AUTHENTICATING }
    }

    fun onAuthSuccess() {
        mutableAuthState.update { AuthState.AUTHENTICATED }
    }
}
