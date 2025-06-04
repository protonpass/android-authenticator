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

package proton.android.authenticator.business.steps.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import proton.android.authenticator.business.shared.domain.infrastructure.preferences.PreferencesDataSource
import proton.android.authenticator.business.steps.application.find.FindStepQuery
import proton.android.authenticator.business.steps.application.find.FindStepQueryHandler
import proton.android.authenticator.business.steps.application.update.UpdateStepCommand
import proton.android.authenticator.business.steps.application.update.UpdateStepCommandHandler
import proton.android.authenticator.business.steps.domain.Step
import proton.android.authenticator.business.steps.domain.StepsRepository
import proton.android.authenticator.business.steps.infrastructure.StepsRepositoryImpl
import proton.android.authenticator.business.steps.infrastructure.preferences.datastore.DataStoreStepPreferencesDataSource
import proton.android.authenticator.shared.common.di.CommandHandlerKey
import proton.android.authenticator.shared.common.di.QueryHandlerKey
import proton.android.authenticator.shared.common.domain.infrastructure.commands.CommandHandler
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryHandler
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class BusinessStepsModule {

    @[Binds Singleton IntoMap QueryHandlerKey(FindStepQuery::class)]
    internal abstract fun bindFindStepQueryHandler(impl: FindStepQueryHandler): QueryHandler<*, *>

    @[Binds Singleton IntoMap CommandHandlerKey(UpdateStepCommand::class)]
    internal abstract fun bindUpdateStepCommandHandler(impl: UpdateStepCommandHandler): CommandHandler<*, *, *>

    @[Binds Singleton]
    internal abstract fun bindStepsRepository(impl: StepsRepositoryImpl): StepsRepository

    @[Binds Singleton]
    internal abstract fun bindStepsPreferencesDataSource(
        impl: DataStoreStepPreferencesDataSource
    ): PreferencesDataSource<Step>

}
