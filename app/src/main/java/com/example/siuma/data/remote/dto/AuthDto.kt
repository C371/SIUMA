package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Profil pengguna — bentuk JSON `user` dari /login, /me, dan /profile. */
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val nim: String?,
    val prodi: String?,
    val nidn: String?,
    val keahlian: String?
) {
    val isDosen: Boolean get() = role == "dosen"
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String?,
    val token: String,
    val user: UserDto
)

/** Respons umum yang hanya berisi pesan. */
data class MessageResponse(
    val message: String?
)

data class MeResponse(
    val user: UserDto
)

data class ProfileUpdateRequest(
    val name: String,
    val email: String
)

data class ProfileUpdateResponse(
    val message: String?,
    val user: UserDto
)

data class PasswordChangeRequest(
    @SerializedName("current_password") val currentPassword: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)
