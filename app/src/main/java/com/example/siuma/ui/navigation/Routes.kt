package com.example.siuma.ui.navigation

sealed class Route(val id: String) {
    object Login : Route("login")
    object SSOLogin : Route("sso_login")
    object GoogleLogin : Route("google_login")
    object Main : Route("main")
    object Jadwal : Route("jadwal")
    object KRS : Route("krs")
    object KHS : Route("khs")
    object Presensi : Route("presensi")
    object Pembayaran : Route("pembayaran")
    
    // contoh navigasi dengan parameter
    data class Detail(val title: String) : Route("detail/$title")
}
