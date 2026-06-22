package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Item daftar kuis (GET /kelas/{id}/kuis). */
data class KuisDto(
    val id: Int,
    val judul: String,
    val deskripsi: String?,
    @SerializedName("jumlah_soal") val jumlahSoal: Int,
    // Mahasiswa
    @SerializedName("sudah_dikerjakan") val sudahDikerjakan: Boolean?,
    val skor: Int?
)

/** Soal pilihan ganda. `jawabanBenar` null untuk mahasiswa (kunci tak dibocorkan). */
data class SoalDto(
    val id: Int,
    val pertanyaan: String,
    @SerializedName("opsi_a") val opsiA: String,
    @SerializedName("opsi_b") val opsiB: String,
    @SerializedName("opsi_c") val opsiC: String,
    @SerializedName("opsi_d") val opsiD: String,
    @SerializedName("jawaban_benar") val jawabanBenar: String?
)

/** Hasil pengerjaan seorang mahasiswa (sisi dosen). */
data class HasilKuisDto(
    val mahasiswa: String?,
    val skor: Int?,
    val benar: Int?,
    val total: Int?,
    @SerializedName("submitted_at") val submittedAt: String?
)

/** Hasil pengerjaan milik mahasiswa yang login. */
data class HasilSayaDto(
    val skor: Int?,
    val benar: Int?,
    val total: Int?,
    @SerializedName("submitted_at") val submittedAt: String?
)

/** Detail kuis (GET /kuis/{id}). */
data class KuisDetailDto(
    val id: Int,
    val judul: String,
    val deskripsi: String?,
    @SerializedName("jumlah_soal") val jumlahSoal: Int,
    val kelas: KelasRefDto,
    val soal: List<SoalDto>?,
    // Mahasiswa
    @SerializedName("sudah_dikerjakan") val sudahDikerjakan: Boolean?,
    @SerializedName("hasil_saya") val hasilSaya: HasilSayaDto?,
    // Dosen
    val hasil: List<HasilKuisDto>?
)

data class KuisCreateRequest(
    val judul: String,
    val deskripsi: String?
)

data class SoalCreateRequest(
    val pertanyaan: String,
    @SerializedName("opsi_a") val opsiA: String,
    @SerializedName("opsi_b") val opsiB: String,
    @SerializedName("opsi_c") val opsiC: String,
    @SerializedName("opsi_d") val opsiD: String,
    @SerializedName("jawaban_benar") val jawabanBenar: String
)

/** Body kerjakan kuis: { "jawaban": { "<soal_id>": "A" } }. */
data class KerjakanRequest(
    val jawaban: Map<String, String>
)

data class KerjakanResponse(
    val message: String?,
    val data: HasilSayaDto?
)
