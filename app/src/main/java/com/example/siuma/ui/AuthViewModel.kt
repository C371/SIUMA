package com.example.siuma.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.local.UserPreferences
import com.example.siuma.data.local.UserSession
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

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
