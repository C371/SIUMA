package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

data class KrsItemDto(
    val kode: String,
    val nama: String,
    val sks: Int,
    val dosen: String?,
    val semester: String?
)

data class KhsItemDto(
    val kode: String,
    val nama: String,
    val sks: Int,
    @SerializedName("nilai_huruf") val nilaiHuruf: String?,
    @SerializedName("nilai_angka") val nilaiAngka: Double?
)

/** GET /akademik (KRS + KHS + total SKS + IP), hanya mahasiswa. */
data class AkademikDto(
    val krs: List<KrsItemDto>,
    val khs: List<KhsItemDto>,
    @SerializedName("total_sks") val totalSks: Int,
    val ip: Double
)

// ---- Rekap Presensi (menu terpisah) ----

/** Baris rekap kehadiran (per mata kuliah utk mhs, atau per mahasiswa utk dosen). */
data class RekapBarisDto(
    val kode: String?,
    val nama: String?,
    val hadir: Int = 0,
    val izin: Int = 0,
    val sakit: Int = 0,
    val alpa: Int = 0,
    val total: Int = 0,
    val persentase: Int = 0,
    // Hanya untuk sisi dosen: daftar mahasiswa per kelas
    val mahasiswa: List<RekapBarisDto>? = null
)

data class RekapResponseDto(
    val rekap: List<RekapBarisDto>
)
