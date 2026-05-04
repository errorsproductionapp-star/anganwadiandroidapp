package com.example.anganwadiapp.presentation.activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class WeeklyActivityUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val anganwadiCenterId: String = "",
    val weekRangeId: String = "",
    val weekDays: List<LocalDate> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeeklyActivityViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyActivityUiState())
    val uiState: StateFlow<WeeklyActivityUiState> = _uiState

    init {
        loadCenterAndWeekInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadCenterAndWeekInfo() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return@launch

            val centerId = appRepository.getAnganwadiCenterId(uid)
                ?: return@launch

            val today = LocalDate.now()
            val monday = today.with(DayOfWeek.MONDAY)
            val friday = monday.plusDays(4)
            val weekDays = (0..4).map { monday.plusDays(it.toLong()) }

            val weekRangeId = buildWeekRangeId(monday, friday)

            _uiState.value = _uiState.value.copy(
                anganwadiCenterId = centerId,
                weekRangeId = weekRangeId,
                weekDays = weekDays
            )
        }
    }

    private fun buildWeekRangeId(start: LocalDate, end: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("ddMM")
        return "${start.format(formatter)}-${end.format(formatter)}"
    }

    fun saveWeeklyPlan(dayActivities: Map<LocalDate, List<DayActivity>>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, saveSuccess = false)

            val state = _uiState.value
            if (state.anganwadiCenterId.isEmpty() || state.weekRangeId.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Center ID not available. Please try again."
                )
                return@launch
            }

            try {
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                dayActivities.forEach { (date, activities) ->
                    val dateStr = date.format(dateFormatter)
                    val activityMaps = activities.map { activity ->
                        mapOf(
                            "id" to activity.id,
                            "activityName" to activity.activityName,
                            "activityType" to activity.activityType.name,
                            "duration" to activity.duration,
                            "materialsNeeded" to activity.materialsNeeded
                        )
                    }

                    appRepository.saveWeeklyActivityPlan(
                        centerId = state.anganwadiCenterId,
                        weekRangeId = state.weekRangeId,
                        date = dateStr,
                        activities = activityMaps
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to save plan"
                )
            }
        }
    }

    fun loadDayActivities(date: LocalDate, onResult: (List<DayActivity>) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.anganwadiCenterId.isEmpty() || state.weekRangeId.isEmpty()) {
                onResult(emptyList())
                return@launch
            }

            val dateStr = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            try {
                val result = appRepository.getWeeklyActivityPlan(
                    centerId = state.anganwadiCenterId,
                    weekRangeId = state.weekRangeId,
                    date = dateStr
                )

                if (result is Result.Success && result.data != null) {
                    @Suppress("UNCHECKED_CAST")
                    val activitiesData = result.data["activities"] as? List<Map<String, Any>> ?: emptyList()
                    val activities = activitiesData.map { data ->
                        DayActivity(
                            id = data["id"] as? String ?: "",
                            activityName = data["activityName"] as? String ?: "",
                            activityType = ActivityType.valueOf(data["activityType"] as? String ?: "EDUCATIONAL"),
                            duration = data["duration"] as? String ?: "",
                            materialsNeeded = data["materialsNeeded"] as? String ?: ""
                        )
                    }
                    onResult(activities)
                } else {
                    onResult(emptyList())
                }
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, errorMessage = null)
    }
}
