package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.dto.TugasDetailDto
import com.example.siuma.data.remote.dto.TugasDto
import com.example.siuma.data.repository.TugasRepository
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState
import com.example.siuma.ui.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TugasViewModel @Inject constructor(
    private val repo: TugasRepository
) : ViewModel() {

    var listState by mutableStateOf<UiState<List<TugasDto>>>(UiState.Loading)
        private set
    var detailState by mutableStateOf<UiState<TugasDetailDto>>(UiState.Loading)
        private set
    var actionState by mutableStateOf<ActionState>(ActionState.Idle)
        private set

    fun loadList(kelasId: Int) {
        viewModelScope.launch {
            listState = UiState.Loading
            listState = repo.getTugas(kelasId).toUiState()
        }
    }

    fun loadDetail(tugasId: Int) {
        viewModelScope.launch {
            detailState = UiState.Loading
            detailState = repo.getTugasDetail(tugasId).toUiState()
        }
    }

    fun submitPengumpulan(
        tugasId: Int,
        jawabanTeks: String?,
        fileBytes: ByteArray?,
        fileName: String?,
        mimeType: String?
    ) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.submitPengumpulan(tugasId, jawabanTeks, fileBytes, fileName, mimeType)) {
                is ApiResult.Success -> { loadDetail(tugasId); ActionState.Success }
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun grade(pengumpulanId: Int, nilai: Int, tugasId: Int) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.gradePengumpulan(pengumpulanId, nilai)) {
                is ApiResult.Success -> { loadDetail(tugasId); ActionState.Success }
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun createTugas(
        kelasId: Int,
        judul: String,
        deskripsi: String?,
        deadline: String?,
        fileBytes: ByteArray?,
        fileName: String?,
        mimeType: String?
    ) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.createTugas(kelasId, judul, deskripsi, deadline, fileBytes, fileName, mimeType)) {
                is ApiResult.Success -> ActionState.Success
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun resetAction() {
        actionState = ActionState.Idle
    }
}
