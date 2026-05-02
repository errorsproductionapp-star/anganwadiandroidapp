package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.remote.dto.toDto
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.ChildRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : ChildRepository {

    override suspend fun saveChildEnrollment(child: Child): Result<String> {
        return try {
            val childId = firestoreDataSource.saveChildEnrollment(child.toDto())
            Result.Success(childId)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save enrollment", e)
        }
    }
}
