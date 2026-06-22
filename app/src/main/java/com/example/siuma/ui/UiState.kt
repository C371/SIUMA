package com.example.siuma.ui

import com.example.siuma.data.remote.ApiResult

/**
 * Status pemuatan satu layar untuk pola loading/sukses/error yang konsisten
 * di seluruh aplikasi (NF-06; mendukung state offline & token kadaluarsa).
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

/** Ubah hasil API menjadi UiState siap-tampil. */
fun <T> ApiResult<T>.toUiState(): UiState<T> = when (this) {
    is ApiResult.Success -> UiState.Success(data)
    is ApiResult.Error -> UiState.Error(message)
}

/**
 * Status operasi tulis (kumpul tugas, beri nilai, buat tugas/kuis, simpan presensi).
 * Berbeda dari UiState karena bersifat transien (one-shot), bukan pemuatan layar.
 */
sealed interface ActionState {
    data object Idle : ActionState
    data object Loading : ActionState
    data object Success : ActionState
    data class Error(val message: String) : ActionState
}
