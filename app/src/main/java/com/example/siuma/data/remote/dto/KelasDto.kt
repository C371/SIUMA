package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Pembungkus respons standar API: { "data": ... }. */
data class DataResponse<T>(val data: T)

/** Item daftar kelas (GET /kelas). */
data class KelasDto(
    val id: Int,
    val kode: String,
    val nama: String,
    val sks: Int,
    val dosen: String?,
    @SerializedName("jumlah_materi") val jumlahMateri: Int?,
    @SerializedName("jumlah_tugas") val jumlahTugas: Int?
)

data class JadwalDto(
    val hari: String?,
    @SerializedName("jam_mulai") val jamMulai: String?,
    @SerializedName("jam_selesai") val jamSelesai: String?,
    val ruang: String?
)

data class MateriDto(
    val id: Int,
    val judul: String,
    val keterangan: String?,
    @SerializedName("file_url") val fileUrl: String?
)

/** Detail kelas (GET /kelas/{id}) + jadwal + daftar materi. */
data class KelasDetailDto(
    val id: Int,
    val kode: String,
    val nama: String,
    val sks: Int,
    val dosen: String?,
    val jadwal: JadwalDto?,
    val materi: List<MateriDto> = emptyList()
)

/** Respons unggah materi (POST /kelas/{id}/materi). */
data class MateriUploadResponse(
    val message: String?,
    val data: MateriDto
)

/** Peserta kelas (GET /kelas/{id}/mahasiswa). */
data class MahasiswaDto(
    val id: Int,
    val nama: String,
    val nim: String?,
    val prodi: String?,
    val semester: String?
)
