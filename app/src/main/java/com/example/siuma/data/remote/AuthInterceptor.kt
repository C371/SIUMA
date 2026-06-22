package com.example.siuma.data.remote

import com.example.siuma.data.local.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Menyisipkan header `Authorization: Bearer <token>` pada tiap permintaan
 * (F-AUTH-03) dan memantau respons 401: bila token tidak valid/kadaluarsa,
 * sesi lokal dihapus sehingga UI otomatis kembali ke layar login (F-AUTH).
 *
 * runBlocking berjalan di thread jaringan OkHttp (bukan main thread); DataStore
 * meng-cache nilainya sehingga pembacaan token sangat cepat.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.currentToken() }

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
        } else {
            chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
        }

        val response = chain.proceed(request)

        // Token kadaluarsa/dicabut → bersihkan sesi (memicu navigasi ke login).
        if (response.code == 401 && token != null) {
            runBlocking { userPreferences.clearSession() }
        }

        return response
    }
}
