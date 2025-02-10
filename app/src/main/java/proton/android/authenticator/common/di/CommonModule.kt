package proton.android.authenticator.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.common.AppDispatchers
import proton.android.authenticator.common.AppDispatchersImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @[Binds Singleton]
    abstract fun bindAppDispatchers(impl: AppDispatchersImpl): AppDispatchers
}
