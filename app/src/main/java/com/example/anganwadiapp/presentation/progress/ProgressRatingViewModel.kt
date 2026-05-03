package com.example.anganwadiapp.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.core.util.ToastHelper
import com.example.anganwadiapp.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ProgressRatingViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val auth: FirebaseAuth,
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressRatingUiState())
    val uiState: StateFlow<ProgressRatingUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val centerId = appRepository.getAnganwadiCenterId(uid) ?: return@launch
            _uiState.update { it.copy(isLoading = true, centerId = centerId) }

            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            _uiState.update { it.copy(todayDate = today) }

            val existingRatings = appRepository.getTodaysProgressRatings(centerId, today)
            val ratingMap = when (existingRatings) {
                is Result.Success -> {
                    existingRatings.data.associate { rating ->
                        val studentId = rating["studentId"] as? String ?: ""
                        studentId to rating
                    }
                }
                else -> emptyMap()
            }

            appRepository.getChildrenByCenter(centerId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val studentList = result.data.map { child ->
                            val existingRating = ratingMap[child.id]
                            StudentRatingItem(
                                id = child.id,
                                name = child.name,
                                initials = getInitials(child.name),
                                age = child.age,
                                foodRating = (existingRating?.get("food") as? Long)?.toInt() ?: 0,
                                educationRating = (existingRating?.get("education") as? Long)?.toInt() ?: 0,
                                activityRating = (existingRating?.get("activity") as? Long)?.toInt() ?: 0,
                                healthRating = (existingRating?.get("health") as? Long)?.toInt() ?: 0,
                                preparednessRating = (existingRating?.get("preparedness") as? Long)?.toInt() ?: 0,
                                isRated = existingRating != null
                            )
                        }
                        _uiState.update {
                            it.copy(
                                students = studentList,
                                isLoading = false,
                                allRated = studentList.all { s -> s.isRated }
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun getInitials(name: String): String {
        return name.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it[0] }
            .joinToString("")
            .uppercase()
    }

    fun updateRating(studentId: String, category: String, rating: Int) {
        _uiState.update { state ->
            val updatedList = state.students.map { student ->
                if (student.id == studentId) {
                    val updated = when (category) {
                        "food" -> student.copy(foodRating = rating)
                        "education" -> student.copy(educationRating = rating)
                        "activity" -> student.copy(activityRating = rating)
                        "health" -> student.copy(healthRating = rating)
                        "preparedness" -> student.copy(preparednessRating = rating)
                        else -> student
                    }
                    val allRated = updated.foodRating > 0 && updated.educationRating > 0 &&
                        updated.activityRating > 0 && updated.healthRating > 0 && updated.preparednessRating > 0
                    updated.copy(isRated = allRated)
                } else student
            }
            state.copy(students = updatedList, allRated = updatedList.all { s -> s.isRated }, isSaved = false)
        }
    }

    fun saveAllRatings() {
        val state = _uiState.value
        val centerId = state.centerId
        if (centerId.isEmpty()) return

        val unrated = state.students.filter {
            it.foodRating == 0 || it.educationRating == 0 || it.activityRating == 0 ||
                it.healthRating == 0 || it.preparednessRating == 0
        }
        if (unrated.isNotEmpty()) {
            toastHelper.showToast("${unrated.size} students still have incomplete ratings")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            var successCount = 0
            var errorCount = 0

            for (student in state.students) {
                val ratingData = mapOf(
                    "food" to student.foodRating,
                    "education" to student.educationRating,
                    "activity" to student.activityRating,
                    "health" to student.healthRating,
                    "preparedness" to student.preparednessRating,
                    "overall" to ((student.foodRating + student.educationRating + student.activityRating +
                        student.healthRating + student.preparednessRating) / 5.0f),
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )

                val saveResult = appRepository.saveProgressRating(
                    centerId = centerId,
                    date = state.todayDate,
                    studentId = student.id,
                    ratingData = ratingData
                )
                when (saveResult) {
                    is Result.Success -> successCount++
                    is Result.Error -> errorCount++
                    Result.Loading -> {}
                }
            }

            if (errorCount == 0) {
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
                toastHelper.showToast("Progress ratings saved for ${successCount} students")
            } else {
                _uiState.update { it.copy(isLoading = false, isSaved = false) }
                toastHelper.showToast("Saved ${successCount}, failed ${errorCount}")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}

data class ProgressRatingUiState(
    val students: List<StudentRatingItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val allRated: Boolean = false,
    val searchQuery: String = "",
    val centerId: String = "",
    val todayDate: String = "",
    val error: String? = null
)

data class StudentRatingItem(
    val id: String,
    val name: String,
    val initials: String,
    val age: String,
    val foodRating: Int = 0,
    val educationRating: Int = 0,
    val activityRating: Int = 0,
    val healthRating: Int = 0,
    val preparednessRating: Int = 0,
    val isRated: Boolean = false
)
