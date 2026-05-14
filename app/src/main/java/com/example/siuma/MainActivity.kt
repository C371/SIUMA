package com.example.siuma

import androidx.activity.compose.BackHandler
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.siuma.ui.navigation.LocalBackStack
import com.example.siuma.ui.navigation.NavDisplay
import com.example.siuma.ui.navigation.Route
import com.example.siuma.ui.screens.*
import com.example.siuma.ui.theme.SIUMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = remember { mutableStateListOf<Route>(Route.Login) }
            
            CompositionLocalProvider(LocalBackStack provides backStack) {
                SIUMATheme {
                    BackHandler(enabled = backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                    NavDisplay(backStack = backStack) { route ->
                        when (route) {
                            is Route.Login -> LoginScreen()
                            is Route.SSOLogin -> SSOLoginScreen()
                            is Route.GoogleLogin -> GoogleLoginScreen()
                            is Route.Main -> MainScreen(isDosen = false)
                            is Route.MainDosen -> MainScreen(isDosen = true)
                            is Route.Jadwal -> JadwalScreen(isDosen = route.isDosen, onBack = { backStack.removeLastOrNull() })
                            is Route.KRS -> KRSScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.KHS -> KHSScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Kelas -> KelasScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Mahasiswa -> MahasiswaScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Penelitian -> PenelitianScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Presensi -> PresensiScreen(isDosen = route.isDosen, onBack = { backStack.removeLastOrNull() })
                            is Route.Pembayaran -> PembayaranScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.SIAKAD -> SIAKADScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Perpustakaan -> LibraryScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Berita -> NewsScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Detail -> DetailScreen(title = route.title, onBack = { backStack.removeLastOrNull() })
                            else -> DetailScreen(title = "Fitur", onBack = { backStack.removeLastOrNull() })
                        }
                    }
                }
            }
        }
    }
}
