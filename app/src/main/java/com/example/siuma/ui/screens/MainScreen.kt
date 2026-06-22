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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") }
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
                    session = session,
                    onOpenKelas = { backStack.add(Route.Kelas) },
                    onOpenAkademik = { backStack.add(Route.Akademik) },
                    onOpenRekap = { backStack.add(Route.Rekap(isDosen)) },
                    onOpenKelasDetail = { id -> backStack.add(Route.KelasDetail(id, isDosen)) },
                    onOpenTugas = { id -> backStack.add(Route.TugasDetail(id, isDosen)) }
                )
                "Profil" -> ProfilScreen(
                    session = session,
                    onLogout = onLogout
                )
            }
        }
    }
}
