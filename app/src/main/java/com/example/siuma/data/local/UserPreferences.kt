package com.example.siuma.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Ekstensi untuk membuat instance DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

data class UserSession(
    val isLoggedIn: Boolean,
    val isDosen: Boolean,
    val userId: String,
    val name: String
)

class UserPreferences(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_DOSEN = booleanPreferencesKey("is_dosen")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // Mengambil data sesi secara real-time sebagai Flow
    val userSessionFlow: Flow<UserSession> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserSession(
                isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
                isDosen = preferences[IS_DOSEN] ?: false,
                userId = preferences[USER_ID] ?: "",
                name = preferences[USER_NAME] ?: ""
            )
        }

    // Fungsi untuk menyimpan data saat login berhasil
    suspend fun saveSession(isDosen: Boolean, userId: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[IS_DOSEN] = isDosen
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
        }
    }

    // Fungsi untuk menghapus data saat logout
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
