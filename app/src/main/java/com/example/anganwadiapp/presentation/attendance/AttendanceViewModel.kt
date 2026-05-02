package com.example.anganwadiapp.presentation.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.core.util.ToastHelper
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val auth: FirebaseAuth,
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val centerId = appRepository.getAnganwadiCenterId(uid) ?: return@launch
            _uiState.update { it.copy(isLoading = true, centerId = centerId, markedBy = uid) }

            val staffResult = appRepository.getStaffByUid(uid)
            val staffName = when (staffResult) {
                is com.example.anganwadiapp.core.common.Result.Success -> staffResult.data.name
                else -> ""
            }
            _uiState.update { it.copy(markedByName = staffName) }
            
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            val existingAttendance = when (val attResult = appRepository.getTodayAttendance(centerId, today)) {
                is com.example.anganwadiapp.core.common.Result.Success -> attResult.data
                else -> null
            }

            val presentIds = if (existingAttendance != null) {
                @Suppress("UNCHECKED_CAST")
                (existingAttendance["presentStudents"] as? List<Map<String, Any>>)?.map { it["id"] as String } ?: emptyList()
            } else emptyList()

            val absentMap = if (existingAttendance != null) {
                @Suppress("UNCHECKED_CAST")
                (existingAttendance["absentStudents"] as? List<Map<String, Any>>)
                    ?.associate { (it["id"] as String) to (it["reason"] as? String ?: "") }
                    ?: emptyMap()
            } else emptyMap()

            appRepository.getChildrenByCenter(centerId).collect { result ->
                when (result) {
                    is com.example.anganwadiapp.core.common.Result.Success -> {
                        val attendanceList = result.data.map { child ->
                            val status = when {
                                presentIds.contains(child.id) -> AttendanceStatus.PRESENT
                                absentMap.containsKey(child.id) -> AttendanceStatus.ABSENT
                                else -> AttendanceStatus.UNMARKED
                            }
                            val reason = absentMap[child.id] ?: ""
                            StudentAttendance(
                                id = child.id,
                                name = child.name,
                                initials = getInitials(child.name),
                                status = status,
                                absentReason = reason
                            )
                        }
                        val allMarked = attendanceList.all { it.status != AttendanceStatus.UNMARKED }
                        _uiState.update { it.copy(students = attendanceList, isLoading = false, isSaved = allMarked) }
                    }
                    is com.example.anganwadiapp.core.common.Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    com.example.anganwadiapp.core.common.Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun updateAbsentReason(studentId: String, reason: String) {
        _uiState.update { state ->
            val updatedList = state.students.map {
                if (it.id == studentId) it.copy(absentReason = reason) else it
            }
            state.copy(students = updatedList)
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

    fun updateStudentStatus(studentId: String, status: AttendanceStatus) {
        _uiState.update { state ->
            val updatedList = state.students.map {
                if (it.id == studentId) it.copy(status = status) else it
            }
            state.copy(students = updatedList, isSaved = false)
        }
    }

    fun markAllPresent() {
        _uiState.update { state ->
            val updatedList = state.students.map { it.copy(status = AttendanceStatus.PRESENT) }
            state.copy(students = updatedList, isSaved = false)
        }
    }

    fun clearAll() {
        _uiState.update { state ->
            val updatedList = state.students.map { it.copy(status = AttendanceStatus.UNMARKED, absentReason = "") }
            state.copy(students = updatedList, isSaved = false)
        }
    }

    fun saveAttendance(markedByName: String) {
        val state = _uiState.value
        val centerId = state.centerId
        if (centerId.isEmpty()) return

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        val presentStudents = state.students
            .filter { it.status == AttendanceStatus.PRESENT }
            .map { mapOf("id" to it.id, "name" to it.name) }

        val absentStudents = state.students
            .filter { it.status == AttendanceStatus.ABSENT }
            .map {
                val studentMap = mutableMapOf<String, Any>("id" to it.id, "name" to it.name)
                if (it.absentReason.isNotBlank()) {
                    studentMap["reason"] = it.absentReason
                }
                studentMap
            }

        val totalStudents = state.students.size
        val totalPresent = presentStudents.size
        val totalAbsent = absentStudents.size

        val unmarked = state.students.filter { it.status == AttendanceStatus.UNMARKED }
        if (unmarked.isNotEmpty()) {
            toastHelper.showToast("${unmarked.size} students still unmarked")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appRepository.saveStudentAttendance(
                centerId, today, totalStudents, totalPresent, totalAbsent,
                markedByName, presentStudents, absentStudents
            )
                .let { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update { it.copy(isLoading = false, isSaved = true) }
                            toastHelper.showToast("Attendance saved successfully")
                        }
                        is Result.Error -> {
                            _uiState.update { it.copy(isLoading = false, error = result.message) }
                            toastHelper.showToast("Failed to save attendance")
                        }
                        Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}

data class AttendanceUiState(
    val students: List<StudentAttendance> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val searchQuery: String = "",
    val centerId: String = "",
    val markedBy: String = "",
    val markedByName: String = "",
    val error: String? = null
)

data class StudentAttendance(
    val id: String,
    val name: String,
    val initials: String,
    val status: AttendanceStatus,
    val absentReason: String = ""
)
