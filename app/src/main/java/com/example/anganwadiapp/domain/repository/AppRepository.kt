package com.example.anganwadiapp.domain.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getChildren(): Flow<Result<List<Child>>>
    suspend fun addChild(child: Child): Result<Unit>
    suspend fun updateChild(child: Child): Result<Unit>
    suspend fun deleteChild(id: String): Result<Unit>
    fun getChildById(id: String): Flow<Result<Child?>>
}
