package com.example.anganwadiapp.presentation.main.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    
    private val _anganwadiCenterId = MutableStateFlow("...")
    val anganwadiCenterId: StateFlow<String> = _anganwadiCenterId
    
    init {
        loadAnganwadiCenterId()
    }
    
    private fun loadAnganwadiCenterId() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val centerId = appRepository.getAnganwadiCenterId(uid)
                _anganwadiCenterId.value = centerId ?: "N/A"
            }
        }
    }
}
