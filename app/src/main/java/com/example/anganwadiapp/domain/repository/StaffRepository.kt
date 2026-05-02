package com.example.anganwadiapp.domain.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Staff

interface StaffRepository {
    suspend fun registerStaff(staff: Staff, password: String): Result<Unit>
    suspend fun loginStaff(email: String, password: String): Result<Staff>
}
