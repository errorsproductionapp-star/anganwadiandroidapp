package com.example.anganwadiapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Provide your dependencies here (Retrofit, Room, etc.)
    /*
    @Provides
    @Singleton
    fun provideMyRepository(): MyRepository {
        return MyRepositoryImpl()
    }
    */
}
