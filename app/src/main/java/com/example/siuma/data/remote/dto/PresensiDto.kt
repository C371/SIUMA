package com.example.siuma.data.remote.dto

import com.google.gson.annotations.SerializedName

// ---- Sisi dosen ----

data class PesertaPresensiDto(
    @SerializedName("krs_id") val krsId: Int,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int,
    val nama: String,
    val nim: String?,
    val status: String?
)

data class PresensiDosenDto(
    val pertemuan: Int,
    @SerializedName("pertemuan_list") val pertemuanList: List<Int>,
    val peserta: List<PesertaPresensiDto>
)

// ---- Sisi mahasiswa ----

data class RekapStatusDto(
    val hadir: Int = 0,
    val izin: Int = 0,
    val sakit: Int = 0,
    val alpa: Int = 0
)

data class PresensiPertemuanDto(
    @SerializedName("pertemuan_ke") val pertemuanKe: Int,
    val tanggal: String?,
    val status: String
)

data class PresensiMahasiswaDto(
    val rekap: RekapStatusDto,
    val pertemuan: List<PresensiPertemuanDto>
)

// ---- Simpan (dosen) ----

data class PresensiSaveRequest(
    @SerializedName("pertemuan_ke") val pertemuanKe: Int,
    val tanggal: String?,
    val status: Map<String, String>
)
