package com.example.anganwadiapp.presentation.auth.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.core.preferences.PreferencesManager
import com.example.anganwadiapp.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ParentViewModel @Inject constructor(
    private val repository: AppRepository,
    val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<ParentLoginState>(ParentLoginState.Idle)
    val loginState: StateFlow<ParentLoginState> = _loginState

    private val _childDetails = MutableStateFlow<Map<String, Any>?>(null)
    val childDetails: StateFlow<Map<String, Any>?> = _childDetails

    fun verifyCredentials(childId: String, dob: String) {
        _loginState.value = ParentLoginState.Loading
        viewModelScope.launch {
            val result = repository.verifyParentCredentials(childId, dob)
            when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        _loginState.value = ParentLoginState.Success(result.data!!)
                    } else {
                        _loginState.value = ParentLoginState.Error("Invalid ID or Date of Birth")
                    }
                }
                is Result.Error -> {
                    _loginState.value = ParentLoginState.Error(result.message ?: "Verification failed")
                }
            }
        }
    }

    fun fetchChildDetails(centerId: String, childId: String) {
        viewModelScope.launch {
            val result = repository.getChildByCenterAndId(centerId, childId)
            if (result is Result.Success) {
                _childDetails.value = result.data
            }
        }
    }

    fun resetState() {
        _loginState.value = ParentLoginState.Idle
        _childDetails.value = null
    }

    private val _attendanceState = MutableStateFlow<ParentAttendanceState>(ParentAttendanceState.Idle)
    val attendanceState: StateFlow<ParentAttendanceState> = _attendanceState

    fun fetchAttendance(centerId: String, childId: String, date: String) {
        _attendanceState.value = ParentAttendanceState.Loading
        viewModelScope.launch {
            val result = repository.getTodayAttendance(centerId, date)
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data == null) {
                        _attendanceState.value = ParentAttendanceState.NoData
                        return@launch
                    }
                    val presentStudents = data["presentStudents"] as? List<*> ?: emptyList<Any>()
                    val absentStudents = data["absentStudents"] as? List<*> ?: emptyList<Any>()

                    val isPresent = presentStudents.any { student ->
                        val map = student as? Map<*, *>
                        map?.get("id").toString() == childId
                    }

                    if (isPresent) {
                        _attendanceState.value = ParentAttendanceState.Loaded(
                            ChildAttendanceStatus(isPresent = true, hasData = true)
                        )
                    } else {
                        val absentEntry = absentStudents.find { student ->
                            val map = student as? Map<*, *>
                            map?.get("id").toString() == childId
                        } as? Map<*, *>
                        val reason = absentEntry?.get("reason")?.toString() ?: ""
                        _attendanceState.value = ParentAttendanceState.Loaded(
                            ChildAttendanceStatus(isPresent = false, absentReason = reason, hasData = true)
                        )
                    }
                }
                is Result.Error -> {
                    _attendanceState.value = ParentAttendanceState.Error(result.message ?: "Failed to fetch attendance")
                }
            }
        }
    }

    fun resetAttendanceState() {
        _attendanceState.value = ParentAttendanceState.Idle
    }

    private val _dietPlanState = MutableStateFlow<ParentDietPlanState>(ParentDietPlanState.Idle)
    val dietPlanState: StateFlow<ParentDietPlanState> = _dietPlanState

    fun fetchDietPlan(centerId: String, date: String) {
        _dietPlanState.value = ParentDietPlanState.Loading
        viewModelScope.launch {
            val result = repository.getDietPlan(centerId, date)
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data == null) {
                        _dietPlanState.value = ParentDietPlanState.NoData
                        return@launch
                    }
                    _dietPlanState.value = ParentDietPlanState.Loaded(data)
                }
                is Result.Error -> {
                    _dietPlanState.value = ParentDietPlanState.Error(result.message ?: "Failed to fetch diet plan")
                }
            }
        }
    }

    fun resetDietPlanState() {
        _dietPlanState.value = ParentDietPlanState.Idle
    }

    private val _ratingState = MutableStateFlow<ParentRatingState>(ParentRatingState.Idle)
    val ratingState: StateFlow<ParentRatingState> = _ratingState

    fun fetchProgressRating(centerId: String, date: String, childId: String) {
        _ratingState.value = ParentRatingState.Loading
        viewModelScope.launch {
            val result = repository.getProgressRating(centerId, date, childId)
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data == null) {
                        _ratingState.value = ParentRatingState.NoData
                        return@launch
                    }
                    _ratingState.value = ParentRatingState.Loaded(data)
                }
                is Result.Error -> {
                    _ratingState.value = ParentRatingState.Error(result.message ?: "Failed to fetch rating")
                }
            }
        }
    }

    fun resetRatingState() {
        _ratingState.value = ParentRatingState.Idle
    }

    private val _healthState = MutableStateFlow<ParentHealthState>(ParentHealthState.Idle)
    val healthState: StateFlow<ParentHealthState> = _healthState

    fun fetchHealthRecord(centerId: String, childId: String, date: String) {
        _healthState.value = ParentHealthState.Loading
        viewModelScope.launch {
            try {
                val dateDocId = date.replace("/", "-")
                val doc = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection(centerId)
                    .document("health_records")
                    .collection(childId)
                    .document(dateDocId)
                    .get()
                    .await()
                if (doc.exists()) {
                    _healthState.value = ParentHealthState.Loaded(doc.data!!)
                } else {
                    _healthState.value = ParentHealthState.NoData
                }
            } catch (e: Exception) {
                _healthState.value = ParentHealthState.Error(e.message ?: "Failed to fetch health record")
            }
        }
    }

    fun resetHealthState() {
        _healthState.value = ParentHealthState.Idle
    }
}

sealed class ParentRatingState {
    object Idle : ParentRatingState()
    object Loading : ParentRatingState()
    data class Loaded(val ratingData: Map<String, Any>) : ParentRatingState()
    data class Error(val message: String) : ParentRatingState()
    object NoData : ParentRatingState()
}

sealed class ParentDietPlanState {
    object Idle : ParentDietPlanState()
    object Loading : ParentDietPlanState()
    data class Loaded(val dietPlan: Map<String, Any>) : ParentDietPlanState()
    data class Error(val message: String) : ParentDietPlanState()
    object NoData : ParentDietPlanState()
}

sealed class ParentLoginState {
    object Idle : ParentLoginState()
    object Loading : ParentLoginState()
    data class Success(val data: Map<String, Any>) : ParentLoginState()
    data class Error(val message: String) : ParentLoginState()
}

data class ChildAttendanceStatus(
    val isPresent: Boolean,
    val absentReason: String = "",
    val hasData: Boolean = true
)

sealed class ParentAttendanceState {
    object Idle : ParentAttendanceState()
    object Loading : ParentAttendanceState()
    data class Loaded(val status: ChildAttendanceStatus) : ParentAttendanceState()
    data class Error(val message: String) : ParentAttendanceState()
    object NoData : ParentAttendanceState()
}

sealed class ParentHealthState {
    object Idle : ParentHealthState()
    object Loading : ParentHealthState()
    data class Loaded(val healthRecord: Map<String, Any>) : ParentHealthState()
    data class Error(val message: String) : ParentHealthState()
    object NoData : ParentHealthState()
}
