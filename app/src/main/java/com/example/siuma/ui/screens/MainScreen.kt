package com.example.siuma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.siuma.data.local.UserSession
import com.example.siuma.ui.navigation.LocalBackStack
import com.example.siuma.ui.navigation.Route

@Composable
fun MainScreen(
    session: UserSession?,
    onLogout: () -> Unit
) {
    val isDosen = session?.isDosen ?: false
    val backStack = LocalBackStack.current
    var currentTab by remember { mutableStateOf("Beranda") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == "Beranda",
                    onClick = { currentTab = "Beranda" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Halaman Awal") },
                    label = { Text("Halaman Awal") }
                )
                NavigationBarItem(
                    selected = currentTab == "Jelajahi",
                    onClick = { currentTab = "Jelajahi" },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Jelajahi") },
                    label = { Text("Umpan") }
                )
                NavigationBarItem(
                    selected = currentTab == "Profil",
                    onClick = { currentTab = "Profil" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil Saya") },
                    label = { Text("Profil Saya") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                "Beranda" -> BerandaScreen(
                    isDosen = isDosen,
                    userName = session?.name ?: "User",
                    userId = session?.userId ?: "",
                    onLogout = onLogout, 
                    onNavigate = { screenTitle ->
                        val route = when(screenTitle) {
                            "Jadwal" -> Route.Jadwal(isDosen)
                            "KRS" -> Route.KRS
                            "KHS" -> Route.KHS
                            "Kelas" -> Route.Kelas
                            "Mahasiswa" -> Route.Mahasiswa
                            "Presensi" -> Route.Presensi(isDosen)
                            "Pembayaran" -> Route.Pembayaran
                            "Penelitian" -> Route.Penelitian
                            "Perpustakaan" -> Route.Perpustakaan
                            "SIAKAD" -> Route.SIAKAD
                            "Prestasi" -> Route.Prestasi
                            "Berita" -> Route.Berita
                            else -> Route.Detail(screenTitle)
                        }
                        backStack.add(route)
                    }
                )
                "Jelajahi" -> JelajahiScreen()
                "Profil" -> ProfilScreen(
                    isDosen = isDosen,
                    userName = session?.name ?: "User",
                    userId = session?.userId ?: ""
                )
            }
        }
    }
}
