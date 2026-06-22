package com.example.siuma.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.data.repository.KelasRepository
import com.example.siuma.data.repository.TugasRepository
import com.example.siuma.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TugasRingkas(val id: Int, val judul: String, val deadline: String?, val kelasNama: String)
data class MateriRingkas(val judul: String, val fileUrl: String?, val kelasNama: String)

data class BerandaData(
    val kelas: List<KelasDto>,
    val tugasMendatang: List<TugasRingkas>,
    val materiTerbaru: List<MateriRingkas>
)

@HiltViewModel
class BerandaViewModel @Inject constructor(
    private val kelasRepo: KelasRepository,
    private val tugasRepo: TugasRepository
) : ViewModel() {

    var state by mutableStateOf<UiState<BerandaData>>(UiState.Loading)
        private set

    fun load(isDosen: Boolean) {
        viewModelScope.launch {
            state = UiState.Loading
            when (val kelasRes = kelasRepo.getKelas()) {
                is ApiResult.Error -> state = UiState.Error(kelasRes.message)
                is ApiResult.Success -> {
                    val kelas = kelasRes.data
                    if (isDosen) {
                        // Dosen: ringkasan = daftar kelas yang diampu (F-HOME-04).
                        state = UiState.Success(BerandaData(kelas, emptyList(), emptyList()))
                    } else {
                        // Mahasiswa: rakit tugas mendatang & materi terbaru lintas kelas (F-HOME-03).
                        val data = coroutineScope {
                            val tugasJobs = kelas.map { k -> async { k to tugasRepo.getTugas(k.id) } }
                            val materiJobs = kelas.map { k -> async { k to kelasRepo.getKelasDetail(k.id) } }

                            val tugas = tugasJobs.awaitAll().flatMap { (k, res) ->
                                (res as? ApiResult.Success)?.data
                                    ?.filter { it.sudahMengumpulkan != true }
                                    ?.map { TugasRingkas(it.id, it.judul, it.deadline, k.nama) }
                                    ?: emptyList()
                            }.sortedWith(compareBy(nullsLast<String>()) { it.deadline }).take(5)

                            val materi = materiJobs.awaitAll().flatMap { (k, res) ->
                                (res as? ApiResult.Success)?.data?.materi
                                    ?.map { MateriRingkas(it.judul, it.fileUrl, k.nama) }
                                    ?: emptyList()
                            }.take(5)

                            BerandaData(kelas, tugas, materi)
                        }
                        state = UiState.Success(data)
                    }
                }
            }
        }
    }
}
