package com.example.anganwadiapp.di

import com.example.anganwadiapp.data.repository.AppRepositoryImpl
import com.example.anganwadiapp.domain.repository.AppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAppRepository(
        repositoryImpl: AppRepositoryImpl
    ): AppRepository
}
