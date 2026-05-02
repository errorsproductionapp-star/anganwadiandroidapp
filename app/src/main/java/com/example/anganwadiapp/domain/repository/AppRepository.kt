package com.example.anganwadiapp.domain.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Staff
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getChildren(): Flow<Result<List<Child>>>
    fun getChildrenByCenter(centerId: String): Flow<Result<List<Child>>>
    suspend fun addChild(child: Child): Result<Unit>
    suspend fun updateChild(child: Child): Result<Unit>
    suspend fun deleteChild(id: String): Result<Unit>
    fun getChildById(id: String): Flow<Result<Child?>>
    suspend fun getAnganwadiCenterId(uid: String): String?
    suspend fun getStaffByUid(uid: String): Result<Staff>
    suspend fun saveStudentAttendance(
        centerId: String,
        date: String,
        totalStudents: Int,
        totalPresent: Int,
        totalAbsent: Int,
        markedBy: String,
        presentStudents: List<Map<String, Any>>,
        absentStudents: List<Map<String, Any>>
    ): Result<Unit>
    suspend fun getTodayAttendance(centerId: String, date: String): Result<Map<String, Any>?>
    suspend fun saveDietPlan(centerId: String, date: String, dietPlan: Map<String, Any>): Result<Unit>
    suspend fun getDietPlan(centerId: String, date: String): Result<Map<String, Any>?>
}
