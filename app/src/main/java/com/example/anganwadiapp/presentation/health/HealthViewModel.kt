package com.example.anganwadiapp.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.domain.model.Child
import com.example.anganwadiapp.domain.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HealthScreenState(
    val centerId: String = "",
    val students: List<Child> = emptyList(),
    val studentsWithRecords: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HealthFormData(
    val studentId: String = "",
    val studentName: String = "",
    val dateOfMeasurement: String = "",
    val height: String = "",
    val weight: String = "",
    val bmiStatus: String = "",
    val vaccinationName: String = "",
    val vaccinationDate: String = "",
    val nextDueDate: String = "",
    val actionTaken: String = "",
    val healthRemarks: String = ""
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(HealthScreenState())
    val state: StateFlow<HealthScreenState> = _state.asStateFlow()

    private val _formData = MutableStateFlow(HealthFormData())
    val formData: StateFlow<HealthFormData> = _formData.asStateFlow()

    init {
        loadCenterAndStudents()
    }

    private fun loadCenterAndStudents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val centerId = appRepository.getAnganwadiCenterId(uid)
                if (centerId != null) {
                    _state.value = _state.value.copy(centerId = centerId)
                    loadStudentsWithNumericIds(centerId)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to get center ID"
                    )
                }
            }
        }
    }

    private fun loadStudentsWithNumericIds(centerId: String) {
        viewModelScope.launch {
            appRepository.getChildrenByCenter(centerId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val numericStudents = result.data.filter { child ->
                            child.id.all { it.isDigit() }
                        }
                        _state.value = _state.value.copy(
                            students = numericStudents,
                            isLoading = false
                        )
                        loadStudentsWithRecords(centerId, numericStudents)
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Result.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun loadStudentsWithRecords(centerId: String, students: List<Child>) {
        viewModelScope.launch {
            val studentsWithRecords = mutableSetOf<String>()
            students.forEach { student ->
                try {
                    val snapshot = firestore.collection(centerId)
                        .document("health_records")
                        .collection(student.id)
                        .get()
                        .await()
                    if (!snapshot.isEmpty) {
                        studentsWithRecords.add(student.id)
                    }
                } catch (e: Exception) {
                }
            }
            _state.value = _state.value.copy(studentsWithRecords = studentsWithRecords)
        }
    }

    fun selectStudent(child: Child) {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val baseFormData = HealthFormData(
                studentId = child.id,
                studentName = child.name,
                dateOfMeasurement = currentDate
            )

            val centerId = _state.value.centerId
            try {
                val snapshot = firestore.collection(centerId)
                    .document("health_records")
                    .collection(child.id)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val latestDoc = snapshot.documents.sortedByDescending { it.id }.first()
                    val data = latestDoc.data
                    if (data != null) {
                        _formData.value = baseFormData.copy(
                            height = if ((data["height"] as? Number)?.toFloat() == 0f) "" else (data["height"] as? Number)?.toFloat()?.toString() ?: "",
                            weight = if ((data["weight"] as? Number)?.toFloat() == 0f) "" else (data["weight"] as? Number)?.toFloat()?.toString() ?: "",
                            bmiStatus = data["bmiStatus"] as? String ?: "",
                            vaccinationName = data["vaccinationName"] as? String ?: "",
                            vaccinationDate = data["vaccinationDate"] as? String ?: "",
                            nextDueDate = data["nextDueDate"] as? String ?: "",
                            actionTaken = data["actionTaken"] as? String ?: "",
                            healthRemarks = data["healthRemarks"] as? String ?: ""
                        )
                        return@launch
                    }
                }
            } catch (e: Exception) {
            }

            _formData.value = baseFormData
        }
    }

    fun updateFormField(update: HealthFormData.() -> HealthFormData) {
        _formData.value = _formData.value.update()
    }

    fun calculateAndSetBmiStatus() {
        val data = _formData.value
        val heightCm = data.height.toFloatOrNull()
        val weightKg = data.weight.toFloatOrNull()

        if (heightCm != null && weightKg != null && heightCm > 0) {
            val heightM = heightCm / 100f
            val bmi = weightKg / (heightM * heightM)
            val status = when {
                bmi < 18.5f -> "Underweight"
                bmi < 25f -> "Normal"
                bmi < 30f -> "Overweight"
                else -> "Obese"
            }
            _formData.value = data.copy(bmiStatus = status)
        } else {
            _formData.value = data.copy(bmiStatus = "")
        }
    }

    fun saveHealthRecord(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val data = _formData.value
            val centerId = _state.value.centerId

            if (data.studentName.isBlank() || data.dateOfMeasurement.isBlank()) {
                onError("Student name and date are required")
                return@launch
            }

            try {
                val healthRecord = mapOf(
                    "studentId" to data.studentId,
                    "studentName" to data.studentName,
                    "dateOfMeasurement" to data.dateOfMeasurement,
                    "height" to (data.height.toFloatOrNull() ?: 0f),
                    "weight" to (data.weight.toFloatOrNull() ?: 0f),
                    "bmiStatus" to data.bmiStatus,
                    "vaccinationName" to data.vaccinationName,
                    "vaccinationDate" to data.vaccinationDate,
                    "nextDueDate" to data.nextDueDate,
                    "actionTaken" to data.actionTaken,
                    "healthRemarks" to data.healthRemarks,
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )

                val dateDocId = data.dateOfMeasurement.replace("/", "-")

                firestore.collection(centerId)
                    .document("health_records")
                    .collection(data.studentId)
                    .document(dateDocId)
                    .set(healthRecord)
                    .await()

                _state.value = _state.value.copy(
                    studentsWithRecords = _state.value.studentsWithRecords + data.studentId
                )

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save health record")
            }
        }
    }

    fun resetForm() {
        _formData.value = HealthFormData()
    }
}
