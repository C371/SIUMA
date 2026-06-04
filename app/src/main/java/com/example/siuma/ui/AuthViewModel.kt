package com.example.siuma.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.local.UserPreferences
import com.example.siuma.data.local.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Ekspos flow DataStore sebagai LiveData agar mudah diobservasi di UI
    val session: LiveData<UserSession> = userPreferences.userSessionFlow.asLiveData()

    fun login(isDosen: Boolean, userId: String, name: String) {
        viewModelScope.launch {
            userPreferences.saveSession(isDosen, userId, name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}
