package com.example.siuma.ui.navigation

sealed class Route(val id: String) {
    object Login : Route("login")
    object Main : Route("main")
    object MainDosen : Route("main_dosen")

    object Akademik : Route("akademik")
    data class Rekap(val isDosen: Boolean = false) : Route("rekap-presensi")

    // Kelas (e-learning inti)
    object Kelas : Route("kelas")
    data class KelasDetail(val kelasId: Int, val isDosen: Boolean = false) : Route("kelas/$kelasId")
    data class Mahasiswa(val kelasId: Int) : Route("kelas/$kelasId/mahasiswa")
    data class MateriUpload(val kelasId: Int) : Route("kelas/$kelasId/materi/upload")

    // Tugas
    data class TugasList(val kelasId: Int, val isDosen: Boolean) : Route("kelas/$kelasId/tugas")
    data class TugasDetail(val tugasId: Int, val isDosen: Boolean) : Route("tugas/$tugasId")
    data class TugasCreate(val kelasId: Int) : Route("kelas/$kelasId/tugas/create")

    // Kuis
    data class KuisList(val kelasId: Int, val isDosen: Boolean) : Route("kelas/$kelasId/kuis")
    data class KuisDetail(val kuisId: Int, val isDosen: Boolean) : Route("kuis/$kuisId")
    data class KuisCreate(val kelasId: Int) : Route("kelas/$kelasId/kuis/create")
    data class SoalCreate(val kuisId: Int) : Route("kuis/$kuisId/soal/create")

    data class Presensi(val kelasId: Int, val isDosen: Boolean = false) : Route("kelas/$kelasId/presensi")

    // contoh navigasi dengan parameter
    data class Detail(val title: String) : Route("detail/$title")
}
