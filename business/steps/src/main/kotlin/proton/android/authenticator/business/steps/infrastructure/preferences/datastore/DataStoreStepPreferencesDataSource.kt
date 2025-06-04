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

package proton.android.authenticator.business.steps.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.business.steps.domain.Step
import proton.android.authenticator.business.steps.domain.StepDestination
import proton.android.authenticator.proto.preferences.settings.StepPreferencesDestination
import proton.android.authenticator.proto.preferences.steps.StepPreferences
import javax.inject.Inject

internal class DataStoreStepPreferencesDataSource @Inject constructor(
    private val stepPreferencesDataStore: DataStore<StepPreferences>
) : PreferencesDataSource<Step> {

    override fun observe(): Flow<Step> = stepPreferencesDataStore.data
        .map { stepPreferences ->
            Step(destination = stepPreferences.destination.toDomain())
        }

    override suspend fun update(step: Step) {
        stepPreferencesDataStore.updateData { stepPreferences ->
            stepPreferences.toBuilder()
                .setDestination(step.destination.toPreferences())
                .build()
        }
    }

    private fun StepPreferencesDestination.toDomain() = when (this) {
        StepPreferencesDestination.STEP_DESTINATION_HOME -> StepDestination.Home
        StepPreferencesDestination.STEP_DESTINATION_ONBOARDING,
        StepPreferencesDestination.UNRECOGNIZED -> StepDestination.Onboarding
    }

    private fun StepDestination.toPreferences() = when (this) {
        StepDestination.Home -> StepPreferencesDestination.STEP_DESTINATION_HOME
        StepDestination.Onboarding -> StepPreferencesDestination.STEP_DESTINATION_ONBOARDING
    }

}
