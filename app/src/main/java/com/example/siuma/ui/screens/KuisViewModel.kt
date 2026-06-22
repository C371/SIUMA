package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.dto.KuisDetailDto
import com.example.siuma.data.remote.dto.KuisDto
import com.example.siuma.data.repository.KuisRepository
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState
import com.example.siuma.ui.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KuisViewModel @Inject constructor(
    private val repo: KuisRepository
) : ViewModel() {

    var listState by mutableStateOf<UiState<List<KuisDto>>>(UiState.Loading)
        private set
    var detailState by mutableStateOf<UiState<KuisDetailDto>>(UiState.Loading)
        private set
    var actionState by mutableStateOf<ActionState>(ActionState.Idle)
        private set

    fun loadList(kelasId: Int) {
        viewModelScope.launch {
            listState = UiState.Loading
            listState = repo.getKuis(kelasId).toUiState()
        }
    }

    fun loadDetail(kuisId: Int) {
        viewModelScope.launch {
            detailState = UiState.Loading
            detailState = repo.getKuisDetail(kuisId).toUiState()
        }
    }

    fun kerjakan(kuisId: Int, jawaban: Map<String, String>) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.kerjakan(kuisId, jawaban)) {
                is ApiResult.Success -> { loadDetail(kuisId); ActionState.Success }
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun createKuis(kelasId: Int, judul: String, deskripsi: String?) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.createKuis(kelasId, judul, deskripsi)) {
                is ApiResult.Success -> ActionState.Success
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun addSoal(
        kuisId: Int,
        pertanyaan: String,
        opsiA: String,
        opsiB: String,
        opsiC: String,
        opsiD: String,
        jawabanBenar: String
    ) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            actionState = when (val res = repo.addSoal(kuisId, pertanyaan, opsiA, opsiB, opsiC, opsiD, jawabanBenar)) {
                is ApiResult.Success -> { loadDetail(kuisId); ActionState.Success }
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun resetAction() {
        actionState = ActionState.Idle
    }
}
