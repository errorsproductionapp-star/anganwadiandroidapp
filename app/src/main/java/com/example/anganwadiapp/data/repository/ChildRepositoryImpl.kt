package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.remote.dto.toDomain
import com.example.anganwadiapp.data.remote.dto.toDto
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun getChildrenByCenter(centerId: String): Flow<List<Child>> {
        return firestoreDataSource.getChildrenByCenterFlow(centerId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun updateChildEnrollment(child: Child): Result<Unit> {
        return try {
            firestoreDataSource.updateChildEnrollment(child.toDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update student", e)
        }
    }

    override suspend fun deleteChildEnrollment(childId: String): Result<Unit> {
        return try {
            firestoreDataSource.deleteChildEnrollment(childId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete student", e)
        }
    }

    override suspend fun getAnganwadiCenterId(): Result<String> {
        return try {
            val uid = firestoreDataSource.getCurrentUid()
                ?: return Result.Error("User not authenticated")
            val centerId = firestoreDataSource.getAnganwadiCenterId(uid)
            if (centerId != null) {
                Result.Success(centerId)
            } else {
                Result.Error("Anganwadi center ID not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get center ID", e)
        }
    }
}
