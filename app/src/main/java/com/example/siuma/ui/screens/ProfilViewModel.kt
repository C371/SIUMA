package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.repository.ProfilRepository
import com.example.siuma.ui.ActionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val repo: ProfilRepository
) : ViewModel() {

    var passwordState by mutableStateOf<ActionState>(ActionState.Idle)
        private set

    fun changePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            passwordState = ActionState.Loading
            passwordState = when (val res = repo.changePassword(current, new, confirm)) {
                is ApiResult.Success -> ActionState.Success
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun resetPasswordState() {
        passwordState = ActionState.Idle
    }
}
