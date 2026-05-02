package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.remote.dto.toDomain
import com.example.anganwadiapp.data.remote.dto.toDto
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : AppRepository {

    override fun getChildren(): Flow<Result<List<Child>>> {
        return firestoreDataSource.getChildrenFlow()
            .map { dtos -> Result.Success(dtos.map { it.toDomain() }) as Result<List<Child>> }
            .catch { e -> emit(Result.Error(e.message ?: "Failed to fetch children", e)) }
    }

    override suspend fun addChild(child: Child): Result<Unit> {
        return try {
            firestoreDataSource.addChild(child.toDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add child", e)
        }
    }

    override suspend fun updateChild(child: Child): Result<Unit> {
        return try {
            firestoreDataSource.updateChild(child.toDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update child", e)
        }
    }

    override suspend fun deleteChild(id: String): Result<Unit> {
        return try {
            firestoreDataSource.deleteChild(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete child", e)
        }
    }

    override fun getChildById(id: String): Flow<Result<Child?>> {
        return firestoreDataSource.getChildByIdFlow(id)
            .map { dto -> Result.Success(dto?.toDomain()) as Result<Child?> }
            .catch { e -> emit(Result.Error(e.message ?: "Failed to fetch child", e)) }
    }
}
