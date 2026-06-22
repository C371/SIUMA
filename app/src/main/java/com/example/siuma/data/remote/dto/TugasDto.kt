package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Ringkasan kelas yang disematkan pada detail tugas/kuis. */
data class KelasRefDto(
    val id: Int,
    val kode: String,
    val nama: String
)

/** Item daftar tugas (GET /kelas/{id}/tugas). Field bergantung peran. */
data class TugasDto(
    val id: Int,
    val judul: String,
    val deskripsi: String?,
    val deadline: String?,
    @SerializedName("file_url") val fileUrl: String?,
    // Mahasiswa
    @SerializedName("sudah_mengumpulkan") val sudahMengumpulkan: Boolean?,
    val nilai: Int?,
    // Dosen
    @SerializedName("jumlah_pengumpulan") val jumlahPengumpulan: Int?
)

/** Pengumpulan milik mahasiswa yang login (sisi mahasiswa). */
data class PengumpulanSayaDto(
    val id: Int,
    @SerializedName("jawaban_teks") val jawabanTeks: String?,
    @SerializedName("file_url") val fileUrl: String?,
    @SerializedName("submitted_at") val submittedAt: String?,
    val nilai: Int?
)

/** Satu baris pengumpulan peserta (sisi dosen). */
data class PengumpulanMahasiswaDto(
    @SerializedName("mahasiswa_id") val mahasiswaId: Int,
    val nama: String,
    val nim: String?,
    @SerializedName("sudah_mengumpulkan") val sudahMengumpulkan: Boolean,
    @SerializedName("pengumpulan_id") val pengumpulanId: Int?,
    @SerializedName("jawaban_teks") val jawabanTeks: String?,
    @SerializedName("file_url") val fileUrl: String?,
    @SerializedName("submitted_at") val submittedAt: String?,
    val nilai: Int?
)

/** Detail tugas (GET /tugas/{id}). */
data class TugasDetailDto(
    val id: Int,
    val judul: String,
    val deskripsi: String?,
    val deadline: String?,
    @SerializedName("file_url") val fileUrl: String?,
    val kelas: KelasRefDto,
    @SerializedName("pengumpulan_saya") val pengumpulanSaya: PengumpulanSayaDto?,
    val pengumpulan: List<PengumpulanMahasiswaDto>?
)

data class NilaiRequest(val nilai: Int)
