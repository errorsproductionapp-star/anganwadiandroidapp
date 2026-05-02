package com.example.anganwadiapp.domain.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child

interface ChildRepository {
    suspend fun saveChildEnrollment(child: Child): Result<String>
}
