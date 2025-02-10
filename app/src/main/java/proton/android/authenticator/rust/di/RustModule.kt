package proton.android.authenticator.rust.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.commonrust.AuthenticatorMobileClient
import proton.android.authenticator.commonrust.AuthenticatorMobileClientInterface
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RustModule {

    @[Provides Singleton]
    fun bindAuthenticatorMobileClient(): AuthenticatorMobileClientInterface = AuthenticatorMobileClient()
}
