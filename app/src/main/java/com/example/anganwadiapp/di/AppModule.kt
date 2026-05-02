package com.example.anganwadiapp.di

import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.repository.AppRepositoryImpl
import com.example.anganwadiapp.data.repository.StaffRepositoryImpl
import com.example.anganwadiapp.domain.repository.AppRepository
import com.example.anganwadiapp.domain.repository.StaffRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideFirebaseFirestore(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }

    @Provides
    @Singleton
    fun provideAppRepository(dataSource: FirestoreDataSource): AppRepository {
        return AppRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideStaffRepository(dataSource: FirestoreDataSource): StaffRepository {
        return StaffRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideApplicationVersion(): String {
        return "2.4.0"
    }
}
