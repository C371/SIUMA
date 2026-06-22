package com.example.siuma.data.repository

import com.example.siuma.data.local.UserPreferences
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.PasswordChangeRequest
import com.example.siuma.data.remote.map
import com.example.siuma.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: UserPreferences
) {

    /** Ganti kata sandi (F-PROF-03). */
    suspend fun changePassword(current: String, new: String, confirm: String): ApiResult<Unit> =
        safeApiCall { api.changePassword(PasswordChangeRequest(current, new, confirm)) }.map { }

    /** Segarkan profil dari server dan simpan ke sesi lokal. */
    suspend fun refreshProfile(): ApiResult<Unit> {
        val res = safeApiCall { api.me() }
        if (res is ApiResult.Success) prefs.updateUser(res.data.user)
        return res.map { }
    }
}
