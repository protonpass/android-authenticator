package proton.android.authenticator.navigation.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.navigation.commands.InMemoryNavigationCommandHandler
import proton.android.authenticator.navigation.domain.commands.NavigationCommandHandler
import proton.android.authenticator.navigation.domain.destinations.NavigationDestination
import proton.android.authenticator.navigation.domain.graphs.home.HomeNavigationDestination
import proton.android.authenticator.navigation.domain.navigators.NavigationNavigator
import proton.android.authenticator.navigation.navigators.AppNavigationNavigator
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal abstract class NavigationModule {

    @[Binds Singleton]
    internal abstract fun bindNavigationNavigator(implementation: AppNavigationNavigator): NavigationNavigator

    @[Binds Singleton]
    internal abstract fun bindNavigationCommandHandler(
        implementation: InMemoryNavigationCommandHandler
    ): NavigationCommandHandler

    internal companion object {

        @[Provides Singleton]
        internal fun provideStartNavigationDestination(): NavigationDestination = HomeNavigationDestination

    }

}
