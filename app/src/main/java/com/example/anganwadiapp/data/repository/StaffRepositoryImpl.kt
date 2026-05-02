package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.remote.dto.toDomain
import com.example.anganwadiapp.data.remote.dto.toDto
import com.example.anganwadiapp.domain.model.Staff
import com.example.anganwadiapp.domain.repository.StaffRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : StaffRepository {

    override suspend fun registerStaff(staff: Staff, password: String): Result<Unit> {
        return try {
            firestoreDataSource.registerStaffWithAuth(staff.email, password, staff.toDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed", e)
        }
    }

    override suspend fun loginStaff(email: String, password: String): Result<Staff> {
        return try {
            firestoreDataSource.loginStaffWithAuth(email, password)
            val currentUid = firestoreDataSource.getCurrentUid()
                ?: return Result.Error("Authentication failed")
            val staffDto = firestoreDataSource.getStaffByUid(currentUid)
            if (staffDto != null) {
                // Capture current date and time for attendance
                val now = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                
                val date = dateFormat.format(now)
                val loginTime = timeFormat.format(now)
                
                // Log attendance: /{centerId}/attendance_records/{date}/{staffName}
                firestoreDataSource.logStaffAttendance(
                    centerId = staffDto.anganwadiCenterId,
                    staffName = staffDto.name,
                    date = date,
                    loginTime = loginTime
                )
                
                Result.Success(staffDto.toDomain())
            } else {
                Result.Error("User not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed", e)
        }
    }
}
