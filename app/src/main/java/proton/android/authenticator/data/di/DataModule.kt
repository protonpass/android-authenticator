package proton.android.authenticator.data.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.data.AppDatabase
import proton.android.authenticator.data.AuthenticatorDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @[Provides Singleton]
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.buildDatabase(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {

    @[Binds Singleton]
    abstract fun provideAuthenticatorDatabase(db: AppDatabase): AuthenticatorDatabase
}
