package com.example.anganwadiapp.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    private val _clearFields = MutableStateFlow(false)
    val clearFields: StateFlow<Boolean> = _clearFields

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun saveReceivedItem(
        itemName: String,
        date: String,
        quantity: String,
        unit: String,
        source: String
    ) {
        val formattedDate = date.replace("/", "-")
        saveStock(
            date = formattedDate,
            stockType = "item_received",
            itemData = mapOf(
                "itemName" to itemName,
                "dateReceived" to formattedDate,
                "quantity" to quantity,
                "unit" to unit,
                "source" to source,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        )
    }

    fun saveUtilizedItem(
        itemName: String,
        date: String,
        quantity: String,
        unit: String,
        usedFor: String
    ) {
        val formattedDate = date.replace("/", "-")
        saveStock(
            date = formattedDate,
            stockType = "item_utilized",
            itemData = mapOf(
                "itemName" to itemName,
                "dateUtilized" to formattedDate,
                "quantity" to quantity,
                "unit" to unit,
                "usedFor" to usedFor,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        )
    }

    private fun saveStock(date: String, stockType: String, itemData: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccess.value = false
            _errorMessage.value = null

            try {
                val uid = firestoreDataSource.getCurrentUid()
                    ?: throw IllegalStateException("User not authenticated")

                val anganwadiCenterId = firestoreDataSource.getAnganwadiCenterId(uid)
                    ?: throw IllegalStateException("Anganwadi center ID not found")

                firestoreDataSource.saveStockItem(
                    anganwadiCenterId = anganwadiCenterId,
                    date = date,
                    stockType = stockType,
                    itemData = itemData
                )

                _saveSuccess.value = true
                _clearFields.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveState() {
        _saveSuccess.value = false
        _errorMessage.value = null
        _clearFields.value = false
    }

    fun clearFieldsHandled() {
        _clearFields.value = false
    }
}
