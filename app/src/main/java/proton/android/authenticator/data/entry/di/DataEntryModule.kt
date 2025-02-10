package proton.android.authenticator.data.entry.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import proton.android.authenticator.data.entry.local.EntryLocalDataSource
import proton.android.authenticator.data.entry.local.EntryLocalDataSourceImpl
import proton.android.authenticator.data.entry.repository.EntryRepository
import proton.android.authenticator.data.entry.repository.EntryRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataEntryModule {

    @[Binds Singleton]
    abstract fun bindEntryRepository(impl: EntryRepositoryImpl): EntryRepository

    @[Binds Singleton]
    abstract fun bindEntryLocalDataSource(impl: EntryLocalDataSourceImpl): EntryLocalDataSource
}
