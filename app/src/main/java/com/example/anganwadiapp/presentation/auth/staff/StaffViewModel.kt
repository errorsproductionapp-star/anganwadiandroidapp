package com.example.anganwadiapp.presentation.auth.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anganwadiapp.core.common.Result
import com.example.anganwadiapp.core.preferences.PreferencesManager
import com.example.anganwadiapp.domain.model.Staff
import com.example.anganwadiapp.domain.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffRepository: StaffRepository,
    val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _registrationState = MutableStateFlow<Result<Unit>?>(null)
    val registrationState = _registrationState.asStateFlow()

    private val _loginState = MutableStateFlow<Result<Staff>?>(null)
    val loginState = _loginState.asStateFlow()

    fun register(staff: Staff, password: String) {
        viewModelScope.launch {
            _registrationState.value = Result.Loading
            _registrationState.value = staffRepository.registerStaff(staff, password)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = staffRepository.loginStaff(email, password)
            _loginState.value = result
            if (result is Result.Success) {
                preferencesManager.setLoggedIn(isLoggedIn = true, email = email)
            }
        }
    }
    
    fun resetRegistrationState() {
        _registrationState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.logout()
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }
}
