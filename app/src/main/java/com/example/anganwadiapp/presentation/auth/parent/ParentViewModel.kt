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
}

sealed class ParentLoginState {
    object Idle : ParentLoginState()
    object Loading : ParentLoginState()
    data class Success(val data: Map<String, Any>) : ParentLoginState()
    data class Error(val message: String) : ParentLoginState()
}
