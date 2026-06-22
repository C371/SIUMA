package com.example.siuma.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.siuma.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Ekstensi untuk membuat instance DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

/**
 * Sesi pengguna yang tersimpan lokal (F-AUTH-03).
 * Token Bearer disisipkan pada setiap permintaan oleh AuthInterceptor.
 */
data class UserSession(
    val token: String,
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val nim: String?,
    val prodi: String?,
    val nidn: String?,
    val keahlian: String?
) {
    val isLoggedIn: Boolean get() = token.isNotBlank()
    val isDosen: Boolean get() = role == "dosen"
}

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_NIM = stringPreferencesKey("user_nim")
        private val USER_PRODI = stringPreferencesKey("user_prodi")
        private val USER_NIDN = stringPreferencesKey("user_nidn")
        private val USER_KEAHLIAN = stringPreferencesKey("user_keahlian")
    }

    // Sesi real-time sebagai Flow; token kosong = belum login.
    val userSessionFlow: Flow<UserSession> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { p ->
            UserSession(
                token = p[TOKEN] ?: "",
                userId = p[USER_ID] ?: "",
                name = p[USER_NAME] ?: "",
                email = p[USER_EMAIL] ?: "",
                role = p[USER_ROLE] ?: "",
                nim = p[USER_NIM],
                prodi = p[USER_PRODI],
                nidn = p[USER_NIDN],
                keahlian = p[USER_KEAHLIAN]
            )
        }

    /** Token saat ini (sekali baca) — dipakai interceptor & cek awal. */
    suspend fun currentToken(): String? = userSessionFlow.first().token.ifBlank { null }

    /** Simpan token + profil saat login berhasil. */
    suspend fun saveSession(token: String, user: UserDto) {
        dataStore.edit { p ->
            p[TOKEN] = token
            writeUser(p, user)
        }
    }

    /** Perbarui profil tanpa mengubah token (mis. setelah edit profil / /me). */
    suspend fun updateUser(user: UserDto) {
        dataStore.edit { p -> writeUser(p, user) }
    }

    /** Hapus seluruh sesi saat logout / token kadaluarsa. */
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    private fun writeUser(p: androidx.datastore.preferences.core.MutablePreferences, user: UserDto) {
        p[USER_ID] = user.id.toString()
        p[USER_NAME] = user.name
        p[USER_EMAIL] = user.email
        p[USER_ROLE] = user.role
        user.nim?.let { p[USER_NIM] = it } ?: p.remove(USER_NIM)
        user.prodi?.let { p[USER_PRODI] = it } ?: p.remove(USER_PRODI)
        user.nidn?.let { p[USER_NIDN] = it } ?: p.remove(USER_NIDN)
        user.keahlian?.let { p[USER_KEAHLIAN] = it } ?: p.remove(USER_KEAHLIAN)
    }
}
