package com.example.anganwadiapp.di

import android.content.Context
import androidx.room.Room
import com.example.anganwadiapp.core.constants.AppConstants
import com.example.anganwadiapp.data.local.AppDatabase
import com.example.anganwadiapp.data.local.dao.ChildDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideChildDao(database: AppDatabase): ChildDao {
        return database.childDao()
    }
}
