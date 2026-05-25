package com.example.siuma

import androidx.activity.compose.BackHandler
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.siuma.ui.AuthViewModel
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
            val authViewModel: AuthViewModel = viewModel()
            val session by authViewModel.session.observeAsState()
            
            val backStack = remember { mutableStateListOf<Route>(Route.Login) }
            
            CompositionLocalProvider(LocalBackStack provides backStack) {
                SIUMATheme {
                    // Logic untuk reaktif routing berdasarkan session
                    val currentSession = session
                    if (currentSession != null) {
                        if (!currentSession.isLoggedIn) {
                            // Jika tidak login, paksa ke Login screen
                            if (backStack.firstOrNull() !is Route.Login && 
                                backStack.firstOrNull() !is Route.SSOLogin && 
                                backStack.firstOrNull() !is Route.GoogleLogin) {
                                backStack.clear()
                                backStack.add(Route.Login)
                            }
                        } else {
                            // Jika login, pastikan di Main screen sesuai peran
                            if (backStack.firstOrNull() !is Route.Main && backStack.firstOrNull() !is Route.MainDosen) {
                                backStack.clear()
                                val startRoute = if (currentSession.isDosen) Route.MainDosen else Route.Main
                                backStack.add(startRoute)
                            }
                        }
                    }

                    BackHandler(enabled = backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }

                    NavDisplay(backStack = backStack) { route ->
                        when (route) {
                            is Route.Login -> LoginScreen(onLoginSuccess = { isDosen, id, name ->
                                authViewModel.login(isDosen, id, name)
                            })
                            is Route.SSOLogin -> SSOLoginScreen(onLoginSuccess = { isDosen, id, name ->
                                authViewModel.login(isDosen, id, name)
                            })
                            is Route.GoogleLogin -> GoogleLoginScreen(onLoginSuccess = { isDosen, id, name ->
                                authViewModel.login(isDosen, id, name)
                            })
                            is Route.Main -> MainScreen(session = currentSession, onLogout = { authViewModel.logout() })
                            is Route.MainDosen -> MainScreen(session = currentSession, onLogout = { authViewModel.logout() })
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
