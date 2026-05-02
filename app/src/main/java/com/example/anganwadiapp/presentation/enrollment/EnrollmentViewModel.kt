package com.example.anganwadiapp.presentation.enrollment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.model.Gender
import com.example.anganwadiapp.domain.repository.ChildRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnrollmentFormData(
    val name: String = "",
    val dateOfBirth: String = "",
    val admissionDate: String = "",
    val dateOfBirthMillis: Long = 0L,
    val admissionDateMillis: Long = 0L,
    val age: String = "",
    val gender: Gender = Gender.OTHER,
    val fatherName: String = "",
    val motherName: String = "",
    val fatherMobile: String = "",
    val motherMobile: String = "",
    val placeOfBirth: String = "",
    val bloodGroup: String = "",
    val physicallyChallenged: Boolean = false,
    val height: String = "",
    val weight: String = "",
    val allergies: String = "",
    val healthNotes: String = ""
)

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val childRepository: ChildRepository
) : ViewModel() {

    private val _formData = MutableStateFlow(EnrollmentFormData())
    val formData = _formData.asStateFlow()

    private val _saveState = MutableStateFlow<Result<String>?>(null)
    val saveState = _saveState.asStateFlow()

    fun updateField(update: EnrollmentFormData.() -> EnrollmentFormData) {
        _formData.value = _formData.value.update()
    }

    fun saveEnrollment() {
        viewModelScope.launch {
            _saveState.value = Result.Loading
            val data = _formData.value

            val child = Child(
                name = data.name,
                dateOfBirth = data.dateOfBirth,
                admissionDate = data.admissionDate,
                age = data.age,
                gender = data.gender,
                fatherName = data.fatherName,
                motherName = data.motherName,
                fatherMobile = data.fatherMobile,
                motherMobile = data.motherMobile,
                placeOfBirth = data.placeOfBirth,
                bloodGroup = data.bloodGroup,
                physicallyChallenged = data.physicallyChallenged,
                height = data.height.toFloatOrNull(),
                weight = data.weight.toFloatOrNull(),
                allergies = data.allergies,
                healthNotes = data.healthNotes
            )

            _saveState.value = childRepository.saveChildEnrollment(child)
        }
    }

    fun resetSaveState() {
        _saveState.value = null
    }

    fun resetForm() {
        _formData.value = EnrollmentFormData()
        _saveState.value = null
    }
}
