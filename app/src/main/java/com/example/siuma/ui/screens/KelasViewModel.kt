package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.dto.KelasDetailDto
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.data.remote.dto.MahasiswaDto
import com.example.siuma.data.repository.KelasRepository
import com.example.siuma.ui.UiState
import com.example.siuma.ui.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KelasViewModel @Inject constructor(
    private val repo: KelasRepository
) : ViewModel() {

    var listState by mutableStateOf<UiState<List<KelasDto>>>(UiState.Loading)
        private set
    var detailState by mutableStateOf<UiState<KelasDetailDto>>(UiState.Loading)
        private set
    var mahasiswaState by mutableStateOf<UiState<List<MahasiswaDto>>>(UiState.Loading)
        private set

    fun loadList() {
        viewModelScope.launch {
            listState = UiState.Loading
            listState = repo.getKelas().toUiState()
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            detailState = UiState.Loading
            detailState = repo.getKelasDetail(id).toUiState()
        }
    }

    fun loadMahasiswa(id: Int) {
        viewModelScope.launch {
            mahasiswaState = UiState.Loading
            mahasiswaState = repo.getKelasMahasiswa(id).toUiState()
        }
    }
}
