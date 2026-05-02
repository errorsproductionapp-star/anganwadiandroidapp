package com.example.anganwadiapp.presentation.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender
import com.example.anganwadiapp.domain.repository.ChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudentsState(
    val students: List<Child> = emptyList(),
    val isLoading: Boolean = true,
    val centerId: String = "",
    val error: String = "",
    val editingStudent: Child? = null,
    val selectedStudent: Child? = null
)

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentsState())
    val state: StateFlow<StudentsState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentsState()
        )

    init {
        loadCenterAndStudents()
    }

    private fun loadCenterAndStudents() {
        viewModelScope.launch {
            when (val centerResult = childRepository.getAnganwadiCenterId()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(centerId = centerResult.data)
                    loadStudents(centerResult.data)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = centerResult.message
                    )
                }
                else -> {}
            }
        }
    }

    private fun loadStudents(centerId: String) {
        viewModelScope.launch {
            childRepository.getChildrenByCenter(centerId).collect { students ->
                _state.value = _state.value.copy(
                    students = students.sortedBy { it.name },
                    isLoading = false,
                    error = ""
                )
            }
        }
    }

    fun startEdit(student: Child) {
        _state.value = _state.value.copy(editingStudent = student)
    }

    fun cancelEdit() {
        _state.value = _state.value.copy(editingStudent = null)
    }

    fun saveEdit(updatedStudent: Child) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (childRepository.updateChildEnrollment(updatedStudent)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        editingStudent = null,
                        error = ""
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = (childRepository.updateChildEnrollment(updatedStudent) as Result.Error).message
                    )
                }
                else -> {}
            }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            childRepository.deleteChildEnrollment(studentId)
        }
    }

    fun selectStudent(student: Child) {
        _state.value = _state.value.copy(selectedStudent = student)
    }

    fun clearSelectedStudent() {
        _state.value = _state.value.copy(selectedStudent = null)
    }
}
