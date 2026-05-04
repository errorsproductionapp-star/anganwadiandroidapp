package com.example.anganwadiapp.data.repository

import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import com.example.anganwadiapp.data.remote.dto.DietPlanDto
import com.example.anganwadiapp.data.remote.dto.toDomain
import com.example.anganwadiapp.data.remote.dto.toDto
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Staff
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

    override fun getChildrenByCenter(centerId: String): Flow<Result<List<Child>>> {
        return firestoreDataSource.getChildrenByCenterFlow(centerId)
            .map { dtos -> Result.Success(dtos.map { it.toDomain() }) as Result<List<Child>> }
            .catch { e -> emit(Result.Error(e.message ?: "Failed to fetch children by center", e)) }
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

    override suspend fun getAnganwadiCenterId(uid: String): String? {
        return firestoreDataSource.getAnganwadiCenterId(uid)
    }

    override suspend fun getStaffByUid(uid: String): Result<Staff> {
        return try {
            val staff = firestoreDataSource.getStaffByUid(uid)
            if (staff != null) {
                Result.Success(staff.toDomain())
            } else {
                Result.Error("Staff not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch staff", e)
        }
    }

    override suspend fun saveStudentAttendance(
        centerId: String,
        date: String,
        totalStudents: Int,
        totalPresent: Int,
        totalAbsent: Int,
        markedBy: String,
        presentStudents: List<Map<String, Any>>,
        absentStudents: List<Map<String, Any>>
    ): Result<Unit> {
        return try {
            firestoreDataSource.saveStudentAttendance(
                centerId, date, totalStudents, totalPresent, totalAbsent,
                markedBy, presentStudents, absentStudents
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save attendance", e)
        }
    }

    override suspend fun getTodayAttendance(centerId: String, date: String): Result<Map<String, Any>?> {
        return try {
            val data = firestoreDataSource.getTodayAttendance(centerId, date)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch attendance", e)
        }
    }

    override suspend fun saveDietPlan(centerId: String, date: String, dietPlan: Map<String, Any>): Result<Unit> {
        return try {
            val dto = DietPlanDto(
                date = date,
                breakfastItems = dietPlan["breakfastItems"] as? List<String> ?: emptyList(),
                breakfastCalories = dietPlan["breakfastCalories"] as? String ?: "",
                breakfastProteins = dietPlan["breakfastProteins"] as? String ?: "",
                breakfastCarbohydrates = dietPlan["breakfastCarbohydrates"] as? String ?: "",
                breakfastFats = dietPlan["breakfastFats"] as? String ?: "",
                breakfastVitamins = dietPlan["breakfastVitamins"] as? String ?: "",
                breakfastMinerals = dietPlan["breakfastMinerals"] as? String ?: "",
                breakfastCompliance = dietPlan["breakfastCompliance"] as? String,
                midDayItems = dietPlan["midDayItems"] as? List<String> ?: emptyList(),
                midDayCalories = dietPlan["midDayCalories"] as? String ?: "",
                midDayProteins = dietPlan["midDayProteins"] as? String ?: "",
                midDayCarbohydrates = dietPlan["midDayCarbohydrates"] as? String ?: "",
                midDayFats = dietPlan["midDayFats"] as? String ?: "",
                midDayVitamins = dietPlan["midDayVitamins"] as? String ?: "",
                midDayMinerals = dietPlan["midDayMinerals"] as? String ?: "",
                midDayCompliance = dietPlan["midDayCompliance"] as? String,
                snackItems = dietPlan["snackItems"] as? List<String> ?: emptyList(),
                snackCalories = dietPlan["snackCalories"] as? String ?: "",
                snackProteins = dietPlan["snackProteins"] as? String ?: "",
                snackCarbohydrates = dietPlan["snackCarbohydrates"] as? String ?: "",
                snackFats = dietPlan["snackFats"] as? String ?: "",
                snackVitamins = dietPlan["snackVitamins"] as? String ?: "",
                snackMinerals = dietPlan["snackMinerals"] as? String ?: "",
                snackCompliance = dietPlan["snackCompliance"] as? String
            )
            firestoreDataSource.saveDietPlan(centerId, date, dto)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save diet plan", e)
        }
    }

    override suspend fun getDietPlan(centerId: String, date: String): Result<Map<String, Any>?> {
        return try {
            val dto = firestoreDataSource.getDietPlan(centerId, date)
            if (dto != null) {
                val map = mutableMapOf<String, Any>()
                map["date"] = dto.date
                map["breakfastItems"] = dto.breakfastItems
                map["breakfastCalories"] = dto.breakfastCalories
                map["breakfastProteins"] = dto.breakfastProteins
                map["breakfastCarbohydrates"] = dto.breakfastCarbohydrates
                map["breakfastFats"] = dto.breakfastFats
                map["breakfastVitamins"] = dto.breakfastVitamins
                map["breakfastMinerals"] = dto.breakfastMinerals
                dto.breakfastCompliance?.let { map["breakfastCompliance"] = it }
                map["midDayItems"] = dto.midDayItems
                map["midDayCalories"] = dto.midDayCalories
                map["midDayProteins"] = dto.midDayProteins
                map["midDayCarbohydrates"] = dto.midDayCarbohydrates
                map["midDayFats"] = dto.midDayFats
                map["midDayVitamins"] = dto.midDayVitamins
                map["midDayMinerals"] = dto.midDayMinerals
                dto.midDayCompliance?.let { map["midDayCompliance"] = it }
                map["snackItems"] = dto.snackItems
                map["snackCalories"] = dto.snackCalories
                map["snackProteins"] = dto.snackProteins
                map["snackCarbohydrates"] = dto.snackCarbohydrates
                map["snackFats"] = dto.snackFats
                map["snackVitamins"] = dto.snackVitamins
                map["snackMinerals"] = dto.snackMinerals
                dto.snackCompliance?.let { map["snackCompliance"] = it }
                Result.Success(map)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch diet plan", e)
        }
    }

    override suspend fun saveWeeklyActivityPlan(
        centerId: String,
        weekRangeId: String,
        date: String,
        activities: List<Map<String, Any>>
    ): Result<Unit> {
        return try {
            firestoreDataSource.saveWeeklyActivityPlan(centerId, weekRangeId, date, activities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save weekly activity plan", e)
        }
    }

    override suspend fun getWeeklyActivityPlan(
        centerId: String,
        weekRangeId: String,
        date: String
    ): Result<Map<String, Any>?> {
        return try {
            val data = firestoreDataSource.getWeeklyActivityPlan(centerId, weekRangeId, date)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch weekly activity plan", e)
        }
    }

    override suspend fun verifyParentCredentials(childId: String, dob: String): Result<Map<String, Any>?> {
        return try {
            val data = firestoreDataSource.verifyParentCredentials(childId, dob)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to verify credentials", e)
        }
    }

    override suspend fun getChildByCenterAndId(centerId: String, childId: String): Result<Map<String, Any>?> {
        return try {
            val data = firestoreDataSource.getChildByCenterAndId(centerId, childId)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch child details", e)
        }
    }

    override suspend fun saveProgressRating(
        centerId: String,
        date: String,
        studentId: String,
        ratingData: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestoreDataSource.saveProgressRating(centerId, date, studentId, ratingData)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save progress rating", e)
        }
    }

    override suspend fun getTodaysProgressRatings(
        centerId: String,
        date: String
    ): Result<List<Map<String, Any>>> {
        return try {
            val data = firestoreDataSource.getTodaysProgressRatings(centerId, date)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch progress ratings", e)
        }
    }

    override suspend fun getProgressRating(
        centerId: String,
        date: String,
        studentId: String
    ): Result<Map<String, Any>?> {
        return try {
            val data = firestoreDataSource.getProgressRating(centerId, date, studentId)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch progress rating", e)
        }
    }
}
