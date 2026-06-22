package com.example.siuma

import androidx.activity.compose.BackHandler
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.ui.AuthViewModel
import com.example.siuma.ui.navigation.LocalBackStack
import com.example.siuma.ui.navigation.NavDisplay
import com.example.siuma.ui.navigation.Route
import com.example.siuma.ui.screens.*
import com.example.siuma.ui.theme.SIUMATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val session by authViewModel.session.observeAsState()
            val loginState by authViewModel.loginState.collectAsState()

            val backStack = remember { mutableStateListOf<Route>(Route.Login) }

            CompositionLocalProvider(LocalBackStack provides backStack) {
                SIUMATheme {
                    // Routing reaktif berdasarkan sesi (token tersimpan / tidak).
                    val currentSession = session
                    if (currentSession != null) {
                        if (!currentSession.isLoggedIn) {
                            // Belum/tidak login → paksa ke Login.
                            if (backStack.firstOrNull() !is Route.Login) {
                                backStack.clear()
                                backStack.add(Route.Login)
                                authViewModel.resetLoginState()
                            }
                        } else {
                            // Sudah login → pastikan berada di Main sesuai peran.
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
                            is Route.Login -> LoginScreen(
                                state = loginState,
                                onLogin = { email, password -> authViewModel.login(email, password) }
                            )
                            is Route.Main -> MainScreen(session = currentSession, onLogout = { authViewModel.logout() })
                            is Route.MainDosen -> MainScreen(session = currentSession, onLogout = { authViewModel.logout() })
                            is Route.Akademik -> AkademikScreen(onBack = { backStack.removeLastOrNull() })
                            is Route.Rekap -> RekapPresensiScreen(isDosen = route.isDosen, onBack = { backStack.removeLastOrNull() })
                            is Route.Kelas -> KelasScreen(
                                isDosen = currentSession?.isDosen ?: false,
                                onBack = { backStack.removeLastOrNull() },
                                onOpenKelas = { id -> backStack.add(Route.KelasDetail(id, currentSession?.isDosen ?: false)) }
                            )
                            is Route.KelasDetail -> KelasDetailScreen(
                                kelasId = route.kelasId,
                                isDosen = route.isDosen,
                                onBack = { backStack.removeLastOrNull() },
                                onOpenMahasiswa = { id -> backStack.add(Route.Mahasiswa(id)) },
                                onOpenTugas = { id -> backStack.add(Route.TugasList(id, route.isDosen)) },
                                onOpenKuis = { id -> backStack.add(Route.KuisList(id, route.isDosen)) },
                                onOpenPresensi = { id -> backStack.add(Route.Presensi(id, route.isDosen)) },
                                onUploadMateri = { id -> backStack.add(Route.MateriUpload(id)) }
                            )
                            is Route.MateriUpload -> MateriUploadScreen(
                                kelasId = route.kelasId,
                                onBack = { backStack.removeLastOrNull() },
                                onUploaded = { backStack.removeLastOrNull() }
                            )
                            is Route.TugasList -> TugasListScreen(
                                kelasId = route.kelasId,
                                isDosen = route.isDosen,
                                onBack = { backStack.removeLastOrNull() },
                                onOpenTugas = { id -> backStack.add(Route.TugasDetail(id, route.isDosen)) },
                                onCreateTugas = { id -> backStack.add(Route.TugasCreate(id)) }
                            )
                            is Route.TugasDetail -> TugasDetailScreen(
                                tugasId = route.tugasId,
                                isDosen = route.isDosen,
                                onBack = { backStack.removeLastOrNull() }
                            )
                            is Route.TugasCreate -> TugasCreateScreen(
                                kelasId = route.kelasId,
                                onBack = { backStack.removeLastOrNull() },
                                onCreated = { backStack.removeLastOrNull() }
                            )
                            is Route.KuisList -> KuisListScreen(
                                kelasId = route.kelasId,
                                isDosen = route.isDosen,
                                onBack = { backStack.removeLastOrNull() },
                                onOpenKuis = { id -> backStack.add(Route.KuisDetail(id, route.isDosen)) },
                                onCreateKuis = { id -> backStack.add(Route.KuisCreate(id)) }
                            )
                            is Route.KuisDetail -> KuisDetailScreen(
                                kuisId = route.kuisId,
                                isDosen = route.isDosen,
                                onBack = { backStack.removeLastOrNull() },
                                onAddSoal = { id -> backStack.add(Route.SoalCreate(id)) }
                            )
                            is Route.KuisCreate -> KuisCreateScreen(
                                kelasId = route.kelasId,
                                onBack = { backStack.removeLastOrNull() },
                                onCreated = { backStack.removeLastOrNull() }
                            )
                            is Route.SoalCreate -> SoalCreateScreen(
                                kuisId = route.kuisId,
                                onBack = { backStack.removeLastOrNull() },
                                onAdded = { backStack.removeLastOrNull() }
                            )
                            is Route.Mahasiswa -> MahasiswaScreen(kelasId = route.kelasId, onBack = { backStack.removeLastOrNull() })
                            is Route.Presensi -> PresensiScreen(kelasId = route.kelasId, isDosen = route.isDosen, onBack = { backStack.removeLastOrNull() })
                            is Route.Detail -> DetailScreen(title = route.title, onBack = { backStack.removeLastOrNull() })
                            else -> DetailScreen(title = "Fitur", onBack = { backStack.removeLastOrNull() })
                        }
                    }
                }
            }
        }
    }
}
