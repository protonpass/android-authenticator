/*
 * Copyright (c) 2026 Proton AG
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

package proton.android.authenticator.features.shared.usecases.featureflag

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.proton.core.featureflag.domain.FeatureFlagManager
import me.proton.core.featureflag.domain.entity.FeatureId
import javax.inject.Inject

class ObserveFeatureFlagUseCase @Inject constructor(
    private val featureFlagManager: FeatureFlagManager,
    private val overridesRepository: FeatureFlagOverridesRepository
) {

    operator fun invoke(flag: FeatureFlag): Flow<Boolean> = combine(
        overridesRepository.overrides,
        featureFlagManager.observe(
            userId = null,
            featureId = FeatureId(id = flag.key)
        )
    ) { overrides, remote ->
        overrides[flag] ?: remote?.value ?: flag.isEnabledDefault
    }

}
