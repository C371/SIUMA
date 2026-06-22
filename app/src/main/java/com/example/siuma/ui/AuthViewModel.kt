package com.example.siuma.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.local.UserPreferences
import com.example.siuma.data.local.UserSession
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Status proses login untuk menggerakkan tombol & pesan error di UI. */
    sealed interface LoginUiState {
        data object Idle : LoginUiState
        data object Loading : LoginUiState
        data object Success : LoginUiState
        data class Error(val message: String) : LoginUiState
    }

    // Sesi DataStore sebagai LiveData; menggerakkan routing reaktif di MainActivity.
    val session: LiveData<UserSession> = userPreferences.userSessionFlow.asLiveData()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginUiState.Error("Email dan kata sandi wajib diisi.")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            _loginState.value = when (val res = authRepository.login(email.trim(), password)) {
                is ApiResult.Success -> LoginUiState.Success
                is ApiResult.Error -> LoginUiState.Error(res.message)
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginUiState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
