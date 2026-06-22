package com.example.siuma.data.repository

import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.HasilSayaDto
import com.example.siuma.data.remote.dto.KerjakanRequest
import com.example.siuma.data.remote.dto.KuisCreateRequest
import com.example.siuma.data.remote.dto.KuisDetailDto
import com.example.siuma.data.remote.dto.KuisDto
import com.example.siuma.data.remote.dto.SoalCreateRequest
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KuisRepository @Inject constructor(private val api: ApiService) {

    suspend fun getKuis(kelasId: Int): ApiResult<List<KuisDto>> =
        safeApiCall { api.getKuis(kelasId) }.map { it.data }

    suspend fun getKuisDetail(kuisId: Int): ApiResult<KuisDetailDto> =
        safeApiCall { api.getKuisDetail(kuisId) }.map { it.data }

    suspend fun createKuis(kelasId: Int, judul: String, deskripsi: String?): ApiResult<Unit> =
        safeApiCall { api.createKuis(kelasId, KuisCreateRequest(judul, deskripsi?.takeIf { it.isNotBlank() })) }.map { }

    suspend fun addSoal(
        kuisId: Int,
        pertanyaan: String,
        opsiA: String,
        opsiB: String,
        opsiC: String,
        opsiD: String,
        jawabanBenar: String
    ): ApiResult<Unit> =
        safeApiCall {
            api.addSoal(kuisId, SoalCreateRequest(pertanyaan, opsiA, opsiB, opsiC, opsiD, jawabanBenar))
        }.map { }

    /** Mahasiswa mengirim jawaban; skor dihitung otomatis di server (F-KUIS-03). */
    suspend fun kerjakan(kuisId: Int, jawaban: Map<String, String>): ApiResult<HasilSayaDto?> =
        safeApiCall { api.kerjakanKuis(kuisId, KerjakanRequest(jawaban)) }.map { it.data }
}
