package com.example.siuma.data.repository

import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.NilaiRequest
import com.example.siuma.data.remote.dto.TugasDetailDto
import com.example.siuma.data.remote.dto.TugasDto
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TugasRepository @Inject constructor(private val api: ApiService) {

    private val plain = "text/plain".toMediaTypeOrNull()

    suspend fun getTugas(kelasId: Int): ApiResult<List<TugasDto>> =
        safeApiCall { api.getTugas(kelasId) }.map { it.data }

    suspend fun getTugasDetail(tugasId: Int): ApiResult<TugasDetailDto> =
        safeApiCall { api.getTugasDetail(tugasId) }.map { it.data }

    /** Dosen membuat tugas (F-TUG-01). File lampiran opsional. */
    suspend fun createTugas(
        kelasId: Int,
        judul: String,
        deskripsi: String?,
        deadline: String?,
        fileBytes: ByteArray?,
        fileName: String?,
        mimeType: String?
    ): ApiResult<Unit> {
        val filePart = if (fileBytes != null) {
            MultipartBody.Part.createFormData(
                "file",
                fileName ?: "lampiran.pdf",
                fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())
            )
        } else null

        return safeApiCall {
            api.createTugas(
                id = kelasId,
                judul = judul.toRequestBody(plain),
                deskripsi = deskripsi?.takeIf { it.isNotBlank() }?.toRequestBody(plain),
                deadline = deadline?.takeIf { it.isNotBlank() }?.toRequestBody(plain),
                file = filePart
            )
        }.map { }
    }

    /** Mahasiswa mengumpulkan tugas (F-TUG-03): teks dan/atau file. */
    suspend fun submitPengumpulan(
        tugasId: Int,
        jawabanTeks: String?,
        fileBytes: ByteArray?,
        fileName: String?,
        mimeType: String?
    ): ApiResult<Unit> {
        val filePart = if (fileBytes != null) {
            MultipartBody.Part.createFormData(
                "file",
                fileName ?: "jawaban",
                fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())
            )
        } else null

        return safeApiCall {
            api.submitPengumpulan(
                id = tugasId,
                jawabanTeks = jawabanTeks?.takeIf { it.isNotBlank() }?.toRequestBody(plain),
                file = filePart
            )
        }.map { }
    }

    /** Dosen memberi nilai pengumpulan (F-TUG-04). */
    suspend fun gradePengumpulan(pengumpulanId: Int, nilai: Int): ApiResult<Unit> =
        safeApiCall { api.gradePengumpulan(pengumpulanId, NilaiRequest(nilai)) }.map { }
}
