package com.example.siuma.data.repository

import com.example.siuma.data.local.UserPreferences
import com.example.siuma.data.remote.ApiResult
import com.example.siuma.data.remote.ApiService
import com.example.siuma.data.remote.dto.LoginRequest
import com.example.siuma.data.remote.dto.UserDto
import com.example.siuma.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sumber kebenaran untuk autentikasi: memanggil API lalu menyimpan/menghapus
 * token + profil di [UserPreferences].
 */
@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: UserPreferences
) {

    /** Login email+password → simpan token & profil bila sukses (F-AUTH-01/03). */
    suspend fun login(email: String, password: String): ApiResult<UserDto> {
        return when (val res = safeApiCall { api.login(LoginRequest(email, password)) }) {
            is ApiResult.Success -> {
                prefs.saveSession(res.data.token, res.data.user)
                ApiResult.Success(res.data.user)
            }
            is ApiResult.Error -> res
        }
    }

    /**
     * Logout (F-AUTH-04). Cabut token di server, lalu hapus sesi lokal.
     * Sesi lokal selalu dibersihkan agar pengguna pasti keluar walau jaringan mati.
     */
    suspend fun logout() {
        safeApiCall { api.logout() }
        prefs.clearSession()
    }
}
