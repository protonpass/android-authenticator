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

package proton.android.authenticator.features.shared.usecases.biometrics

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import proton.android.authenticator.business.biometrics.application.find.FindBiometricQuery
import proton.android.authenticator.business.biometrics.domain.Biometric
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus

class ObserveBiometricUseCase @Inject constructor(private val queryBus: QueryBus) {

    operator fun invoke(): Flow<Biometric> = FindBiometricQuery
        .let { query -> queryBus.ask<Biometric>(query) }

}
