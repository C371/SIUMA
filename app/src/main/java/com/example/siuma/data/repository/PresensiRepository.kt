package com.example.siuma.data.repository

import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.PresensiDosenDto
import com.example.siuma.data.remote.dto.PresensiMahasiswaDto
import com.example.siuma.data.remote.dto.PresensiSaveRequest
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresensiRepository @Inject constructor(private val api: ApiService) {

    suspend fun getPresensiDosen(kelasId: Int, pertemuan: Int?): ApiResult<PresensiDosenDto> =
        safeApiCall { api.getPresensiDosen(kelasId, pertemuan) }.map { it.data }

    suspend fun getPresensiMahasiswa(kelasId: Int): ApiResult<PresensiMahasiswaDto> =
        safeApiCall { api.getPresensiMahasiswa(kelasId) }.map { it.data }

    /** Dosen menyimpan presensi satu pertemuan (F-PRES-01). */
    suspend fun savePresensi(
        kelasId: Int,
        pertemuanKe: Int,
        tanggal: String?,
        status: Map<String, String>
    ): ApiResult<Unit> =
        safeApiCall {
            api.savePresensi(kelasId, PresensiSaveRequest(pertemuanKe, tanggal, status))
        }.map { }
}
