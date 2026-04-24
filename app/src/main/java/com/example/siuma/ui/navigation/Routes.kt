package com.example.siuma.ui.navigation

sealed class Route(val id: String) {
    object Login : Route("login")
    object SSOLogin : Route("sso_login")
    object GoogleLogin : Route("google_login")
    object Main : Route("main")

    object MainDosen : Route("main_dosen")
    object Jadwal : Route("jadwal")
    object KRS : Route("krs")
    object KHS : Route("khs")
    object  Kelas : Route("kelas")
    object Mahasiswa : Route("mahasiswa")
    object Penelitian : Route("penelitian")
    object Presensi : Route("presensi")
    object Pembayaran : Route("pembayaran")
    object Perpustakaan : Route("perpustakaan")
    object SIAKAD : Route("siakad")
    object Prestasi : Route("prestasi")
    object Berita : Route("berita")
    
    // contoh navigasi dengan parameter
    data class Detail(val title: String) : Route("detail/$title")
}
