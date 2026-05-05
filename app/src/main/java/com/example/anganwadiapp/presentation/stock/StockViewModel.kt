package com.example.anganwadiapp.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.data.remote.FirestoreDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockItem(
    val documentId: String = "",
    val dateReceived: String = "",
    val itemName: String = "",
    val quantity: String = "",
    val source: String = "",
    val unit: String = ""
)

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

    private val _stockDates = MutableStateFlow<List<String>>(emptyList())
    val stockDates: StateFlow<List<String>> = _stockDates

    private val _stockItems = MutableStateFlow<List<StockItem>>(emptyList())
    val stockItems: StateFlow<List<StockItem>> = _stockItems

    private val _isStocksLoading = MutableStateFlow(false)
    val isStocksLoading: StateFlow<Boolean> = _isStocksLoading

    fun loadStockDates() {
        viewModelScope.launch {
            _isStocksLoading.value = true
            _errorMessage.value = null

            try {
                val uid = firestoreDataSource.getCurrentUid()
                    ?: throw IllegalStateException("User not authenticated")

                val anganwadiCenterId = firestoreDataSource.getAnganwadiCenterId(uid)
                    ?: throw IllegalStateException("Anganwadi center ID not found")

                android.util.Log.d("StockViewModel", "Loading dates for center: $anganwadiCenterId")

                val dates = firestoreDataSource.getStockItemDates(
                    anganwadiCenterId = anganwadiCenterId,
                    stockType = "item_received"
                )

                android.util.Log.d("StockViewModel", "Found dates: $dates")
                _stockDates.value = dates
            } catch (e: Exception) {
                android.util.Log.e("StockViewModel", "Error loading dates: ${e.message}", e)
                _errorMessage.value = e.message
            } finally {
                _isStocksLoading.value = false
            }
        }
    }

    fun loadStockItemsForDate(date: String) {
        viewModelScope.launch {
            _isStocksLoading.value = true
            _errorMessage.value = null

            try {
                val uid = firestoreDataSource.getCurrentUid()
                    ?: throw IllegalStateException("User not authenticated")

                val anganwadiCenterId = firestoreDataSource.getAnganwadiCenterId(uid)
                    ?: throw IllegalStateException("Anganwadi center ID not found")

                android.util.Log.d("StockViewModel", "Loading items for date: $date, center: $anganwadiCenterId")

                val items = firestoreDataSource.getStockItemsForDate(
                    anganwadiCenterId = anganwadiCenterId,
                    stockType = "item_received",
                    date = date
                )

                android.util.Log.d("StockViewModel", "Found ${items.size} items for date $date")

                _stockItems.value = items.map { data ->
                    StockItem(
                        documentId = data["documentId"] as? String ?: "",
                        dateReceived = data["dateReceived"] as? String ?: "",
                        itemName = data["itemName"] as? String ?: "",
                        quantity = data["quantity"] as? String ?: "",
                        source = data["source"] as? String ?: "",
                        unit = data["unit"] as? String ?: ""
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("StockViewModel", "Error loading items: ${e.message}", e)
                _errorMessage.value = e.message
            } finally {
                _isStocksLoading.value = false
            }
        }
    }

    fun updateStockItem(
        item: StockItem,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val uid = firestoreDataSource.getCurrentUid()
                    ?: throw IllegalStateException("User not authenticated")

                val anganwadiCenterId = firestoreDataSource.getAnganwadiCenterId(uid)
                    ?: throw IllegalStateException("Anganwadi center ID not found")

                val data = mapOf(
                    "itemName" to item.itemName,
                    "dateReceived" to item.dateReceived,
                    "quantity" to item.quantity,
                    "unit" to item.unit,
                    "source" to item.source
                )

                firestoreDataSource.updateStockItem(
                    anganwadiCenterId = anganwadiCenterId,
                    stockType = "item_received",
                    date = item.dateReceived,
                    documentId = item.documentId,
                    itemData = data
                )

                _saveSuccess.value = true
                loadStockItemsForDate(item.dateReceived)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
                onDone()
            }
        }
    }

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
