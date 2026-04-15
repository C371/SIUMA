package com.example.siuma.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.siuma.R

@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Halaman Awal") },
                    label = { Text("Halaman Awal") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Jelajahi") },
                    label = { Text("Jelajahi") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil Saya") },
                    label = { Text("Profil Saya") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> BerandaScreen(onLogout)
                1 -> JelajahiScreen()
                2 -> ProfilScreen()
            }
        }
    }
}

@Composable
fun BerandaScreen(onLogout: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B194C))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logosimua),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("SIMUA", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Universitas Sebelas Maret", color = Color(0xFFFFC107), fontSize = 11.sp)
            }
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text("Keluar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B194C)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pasfoto),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Daffa Dewanda Putra", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("NIM: L0124094", color = Color(0xFFFFC107), fontSize = 13.sp)
                    Text("Program Studi S-1 Informatika", color = Color.White, fontSize = 12.sp)
                    Text("Fakultas Teknologi Informasi dan Sains Data (FATISDA)", color = Color.White, fontSize = 11.sp)
                    Text("Status: Aktif", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Grid Menu
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            val menuItems = listOf(
                MenuItem("Jadwal", Icons.Default.DateRange),
                MenuItem("KRS", Icons.AutoMirrored.Filled.Assignment),
                MenuItem("KHS", Icons.Default.Description),
                MenuItem("Presensi", Icons.AutoMirrored.Filled.FactCheck),
                MenuItem("Pembayaran", Icons.Default.Payments),
                MenuItem("Perpustakaan", Icons.AutoMirrored.Filled.LibraryBooks),
                MenuItem("SIAKAD", Icons.Default.School),
                MenuItem("Prestasi", Icons.Default.EmojiEvents),
                MenuItem("Berita", Icons.Default.Newspaper)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Menggunakan Row dengan weight agar sejajar sempurna
                menuItems.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { item ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MenuIcon(item)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Kelas Mendatang
        Text(
            "Kelas Mendatang",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF0B194C)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pengembangan Aplikasi Bergerak", fontWeight = FontWeight.Bold, color = Color(0xFF0B194C))
                Text("12013220427 - Arif Rohmadi S.Kom., M.Cs.", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("B4.11", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

data class MenuItem(val title: String, val icon: ImageVector)

@Composable
fun MenuIcon(item: MenuItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFFF0F4F8), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF0B194C)
            )
        }
        Text(item.title, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun JelajahiScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Jelajahi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B194C))
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B194C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Temukan program impian dan informasi pendaftaran UNS di sini.", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, "https://uns.ac.id/id/informasi-akademik/fakultas".toUri())) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Fakultas dari UNS")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, "https://uns.ac.id/id/informasi-akademik/daftar-program-studi".toUri())) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Program Studi")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, "https://spmb.uns.ac.id".toUri())) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Pendaftaran")
                }
            }
        }
    }
}

@Composable
fun ProfilScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman Profil Saya (Belum diimplementasikan)")
    }
}