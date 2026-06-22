package com.example.siuma.data.repository

import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.KelasDetailDto
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.data.remote.dto.MahasiswaDto
import com.example.siuma.data.remote.dto.MateriDto
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KelasRepository @Inject constructor(private val api: ApiService) {

    suspend fun getKelas(): ApiResult<List<KelasDto>> =
        safeApiCall { api.getKelas() }.map { it.data }

    suspend fun getKelasDetail(id: Int): ApiResult<KelasDetailDto> =
        safeApiCall { api.getKelasDetail(id) }.map { it.data }

    suspend fun getKelasMahasiswa(id: Int): ApiResult<List<MahasiswaDto>> =
        safeApiCall { api.getKelasMahasiswa(id) }.map { it.data }

    /** Unggah materi PDF (F-MAT-01). File sudah dibaca menjadi bytes oleh pemanggil. */
    suspend fun uploadMateri(
        kelasId: Int,
        judul: String,
        keterangan: String?,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): ApiResult<MateriDto> {
        val plain = "text/plain".toMediaTypeOrNull()
        val judulBody = judul.toRequestBody(plain)
        val keteranganBody = keterangan?.takeIf { it.isNotBlank() }?.toRequestBody(plain)
        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        )
        return safeApiCall { api.uploadMateri(kelasId, judulBody, keteranganBody, filePart) }
            .map { it.data }
    }
}
