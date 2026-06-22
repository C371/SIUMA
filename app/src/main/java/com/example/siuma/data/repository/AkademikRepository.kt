package com.example.siuma.data.repository

import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.AkademikDto
import com.example.siuma.data.remote.dto.RekapBarisDto
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AkademikRepository @Inject constructor(private val api: ApiService) {

    suspend fun getAkademik(): ApiResult<AkademikDto> =
        safeApiCall { api.getAkademik() }.map { it.data }

    suspend fun getRekapPresensi(): ApiResult<List<RekapBarisDto>> =
        safeApiCall { api.getRekapPresensi() }.map { it.data.rekap }
}
