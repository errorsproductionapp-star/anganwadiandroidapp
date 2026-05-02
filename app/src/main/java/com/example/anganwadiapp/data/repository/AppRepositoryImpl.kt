package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.local.dao.ChildDao
import com.example.anganwadiapp.data.local.entity.toDomain
import com.example.anganwadiapp.data.local.entity.toEntity
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val childDao: ChildDao
) : AppRepository {

    override fun getChildren(): Flow<Result<List<Child>>> {
        return childDao.getAllChildren()
            .map { entities -> Result.Success(entities.map { it.toDomain() }) as Result<List<Child>> }
            .catch { e -> emit(Result.Error(e.message ?: "Unknown error", e)) }
    }

    override suspend fun addChild(child: Child): Result<Unit> {
        return try {
            childDao.insertChild(child.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add child", e)
        }
    }

    override suspend fun updateChild(child: Child): Result<Unit> {
        return try {
            childDao.updateChild(child.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update child", e)
        }
    }

    override suspend fun deleteChild(id: String): Result<Unit> {
        return try {
            val childEntity = childDao.getChildByIdOnce(id)
            childEntity?.let { childDao.deleteChild(it) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete child", e)
        }
    }

    override fun getChildById(id: String): Flow<Result<Child?>> {
        return childDao.getChildById(id)
            .map { entity -> Result.Success(entity?.toDomain()) as Result<Child?> }
            .catch { e -> emit(Result.Error(e.message ?: "Unknown error", e)) }
    }
}
