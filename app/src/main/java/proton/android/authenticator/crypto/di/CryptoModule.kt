package proton.android.authenticator.crypto.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.crypto.EncryptionContextProvider
import proton.android.authenticator.crypto.EncryptionContextProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {

    @[Binds Singleton]
    abstract fun bindEncryptionContextProvider(impl: EncryptionContextProviderImpl): EncryptionContextProvider
}
