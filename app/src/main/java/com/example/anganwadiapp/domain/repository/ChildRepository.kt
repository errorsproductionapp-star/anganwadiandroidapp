package com.example.anganwadiapp.domain.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import kotlinx.coroutines.flow.Flow

interface ChildRepository {
    suspend fun saveChildEnrollment(child: Child): Result<String>
    fun getChildrenByCenter(centerId: String): Flow<List<Child>>
    suspend fun updateChildEnrollment(child: Child): Result<Unit>
    suspend fun deleteChildEnrollment(childId: String): Result<Unit>
    suspend fun getAnganwadiCenterId(): Result<String>
}
