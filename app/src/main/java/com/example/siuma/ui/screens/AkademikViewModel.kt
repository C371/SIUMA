package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.dto.AkademikDto
import com.example.siuma.data.remote.dto.RekapBarisDto
import com.example.siuma.data.repository.AkademikRepository
import com.example.siuma.ui.UiState
import com.example.siuma.ui.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AkademikViewModel @Inject constructor(
    private val repo: AkademikRepository
) : ViewModel() {

    var akademikState by mutableStateOf<UiState<AkademikDto>>(UiState.Loading)
        private set
    var rekapState by mutableStateOf<UiState<List<RekapBarisDto>>>(UiState.Loading)
        private set

    fun loadAkademik() {
        viewModelScope.launch {
            akademikState = UiState.Loading
            akademikState = repo.getAkademik().toUiState()
        }
    }

    fun loadRekap() {
        viewModelScope.launch {
            rekapState = UiState.Loading
            rekapState = repo.getRekapPresensi().toUiState()
        }
    }
}
