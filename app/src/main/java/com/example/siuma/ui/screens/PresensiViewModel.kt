package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.dto.PresensiDosenDto
import com.example.siuma.data.remote.dto.PresensiMahasiswaDto
import com.example.siuma.data.repository.PresensiRepository
import com.example.siuma.ui.ActionState
import com.example.siuma.ui.UiState
import com.example.siuma.ui.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresensiViewModel @Inject constructor(
    private val repo: PresensiRepository
) : ViewModel() {

    // Mahasiswa
    var mahasiswaState by mutableStateOf<UiState<PresensiMahasiswaDto>>(UiState.Loading)
        private set

    // Dosen
    var dosenState by mutableStateOf<UiState<PresensiDosenDto>>(UiState.Loading)
        private set
    var selectedPertemuan by mutableStateOf(1)
        private set
    val statuses = mutableStateMapOf<String, String>() // krs_id -> status
    var actionState by mutableStateOf<ActionState>(ActionState.Idle)
        private set

    fun loadMahasiswa(kelasId: Int) {
        viewModelScope.launch {
            mahasiswaState = UiState.Loading
            mahasiswaState = repo.getPresensiMahasiswa(kelasId).toUiState()
        }
    }

    fun loadDosen(kelasId: Int, pertemuan: Int?) {
        viewModelScope.launch {
            dosenState = UiState.Loading
            val res = repo.getPresensiDosen(kelasId, pertemuan)
            dosenState = res.toUiState()
            if (res is ApiResult.Success) {
                selectedPertemuan = res.data.pertemuan
                statuses.clear()
                res.data.peserta.forEach { p ->
                    p.status?.let { statuses[p.krsId.toString()] = it }
                }
            }
        }
    }

    /** Pilih pertemuan yang sudah ada → muat statusnya. */
    fun selectPertemuan(kelasId: Int, pertemuan: Int) = loadDosen(kelasId, pertemuan)

    /** Siapkan pertemuan baru (nomor berikutnya) dengan status kosong. */
    fun newPertemuan() {
        val max = (dosenState as? UiState.Success)?.data?.pertemuanList?.maxOrNull() ?: 0
        selectedPertemuan = max + 1
        statuses.clear()
    }

    fun setStatus(krsId: Int, status: String) {
        statuses[krsId.toString()] = status
    }

    fun save(kelasId: Int) {
        viewModelScope.launch {
            actionState = ActionState.Loading
            val tanggal = java.time.LocalDate.now().toString()
            actionState = when (val res = repo.savePresensi(kelasId, selectedPertemuan, tanggal, statuses.toMap())) {
                is ApiResult.Success -> { loadDosen(kelasId, selectedPertemuan); ActionState.Success }
                is ApiResult.Error -> ActionState.Error(res.message)
            }
        }
    }

    fun resetAction() {
        actionState = ActionState.Idle
    }
}
