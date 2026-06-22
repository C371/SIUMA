package com.example.siuma.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import java.io.IOException

/**
 * Hasil pemanggilan API yang aman dipakai di ViewModel/UI.
 * - [Success] membawa data.
 * - [Error] membawa kode HTTP (bila ada) dan pesan ramah untuk ditampilkan.
 */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(
        val code: Int? = null,
        val message: String
    ) : ApiResult<Nothing>
}

/** Transformasi data pada Success; Error diteruskan apa adanya. */
inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Error -> this
}

/** Bentuk badan error standar Laravel: { message, errors: { field: [..] } }. */
private data class ApiErrorBody(
    val message: String?,
    val errors: Map<String, List<String>>?
)

/**
 * Membungkus panggilan Retrofit menjadi [ApiResult], menangani:
 * - sukses (2xx) → Success
 * - error HTTP (4xx/5xx) → Error dengan pesan dari badan respons bila ada
 * - jaringan mati / timeout → Error "Tidak dapat terhubung ke server"
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error(response.code(), "Respons kosong dari server.")
            }
        } else {
            ApiResult.Error(response.code(), parseError(response))
        }
    } catch (e: IOException) {
        ApiResult.Error(null, "Tidak dapat terhubung ke server. Periksa koneksi.")
    } catch (e: Exception) {
        ApiResult.Error(null, "Terjadi kesalahan: ${e.localizedMessage ?: "tidak diketahui"}")
    }
}

private fun parseError(response: Response<*>): String {
    val raw = try {
        response.errorBody()?.string()
    } catch (e: Exception) {
        null
    }
    if (raw.isNullOrBlank()) return defaultMessageFor(response.code())

    return try {
        val parsed = Gson().fromJson(raw, ApiErrorBody::class.java)
        // Utamakan pesan validasi field pertama bila ada, lalu pesan umum.
        parsed?.errors?.values?.firstOrNull()?.firstOrNull()
            ?: parsed?.message
            ?: defaultMessageFor(response.code())
    } catch (e: JsonSyntaxException) {
        defaultMessageFor(response.code())
    }
}

private fun defaultMessageFor(code: Int): String = when (code) {
    401 -> "Sesi berakhir. Silakan masuk kembali."
    403 -> "Anda tidak memiliki akses untuk tindakan ini."
    404 -> "Data tidak ditemukan."
    409 -> "Tindakan ini sudah pernah dilakukan."
    422 -> "Data yang dikirim tidak valid."
    in 500..599 -> "Server sedang bermasalah. Coba lagi nanti."
    else -> "Terjadi kesalahan (kode $code)."
}
