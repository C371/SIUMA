package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.example.siuma.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
    isDosen: Boolean,
    userName: String,
    userId: String,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar dari Akun?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 75.dp,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetShadowElevation = 8.dp,
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 600.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 40.dp, height = 4.dp)
                                .background(Color.LightGray, CircleShape)
                        )
                    }
                    Text(
                        if (isDosen) "Jadwal Mengajar" else "Kelas Mendatang",
                        modifier = Modifier.padding(bottom = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0B194C)
                    )
                }
                
                items(8) { index ->
                    val subjects = listOf(
                        "Pengembangan Aplikasi Bergerak",
                        "Kecerdasan Buatan",
                        "Basis Data",
                        "Sistem Operasi",
                        "Teori Bahasa & Automata"
                    )
                    val rooms = listOf(
                        "B4.11",
                        "B4.04",
                        "Lab Komputer 1",
                        "B4.05",
                        "B4.11"
                    )
                    val dosenList = listOf(
                        "Arif Rohmadi S.Kom., M.Cs",
                        "Akhmad Syaifuddin S.Si., M.Cs.",
                        "Bambang Widoyono S.T., M.T.I.",
                        "Herdito Ibnu Dewangkoro M.Kom.",
                        "HERI PRASETYO S.Kom, M.Sc.Eng., Ph"
                    )

                    val semester = listOf(
                        "Semester 1",
                        "Semester 2",
                        "Semester 3",
                        "Semester 4",
                        "Semester 5",
                        "Semester 6",
                        "Semester 7",
                        "Semester 8"
                    )
                    
                    KelasMendatangItem(
                        title = subjects[index % subjects.size],
                        subtitle = if (isDosen) "Informatika - Semester ${semester[index % semester.size]}" else "1201322042${7 + (index % 4)} - ${dosenList[index % dosenList.size]}",
                        room = rooms[index % rooms.size]
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B194C))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.logosimua)
                        .crossfade(true)
                        .precision(Precision.EXACT)
                        .build(),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("SIMUA", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Universitas Sebelas Maret", color = Color(0xFFFFC107), fontSize = 11.sp)
                }
                Button(
                    onClick = { showLogoutDialog = true },
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
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.pasfoto)
                            .crossfade(true)
                            .precision(Precision.EXACT)
                            .build(),
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${if (isDosen) "NIP" else "NIM"}: $userId", color = Color(0xFFFFC107), fontSize = 13.sp)
                        Text("Program Studi Informatika", color = Color.White, fontSize = 12.sp)
                        Text("Fakultas Teknologi Informasi dan Data", color = Color.White, fontSize = 11.sp)
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
                val menuItems = if (isDosen) {
                    listOf(
                        MenuItem("Jadwal", Icons.Default.DateRange),
                        MenuItem("Kelas", Icons.Default.School),
                        MenuItem("Mahasiswa", Icons.Default.Groups),
                        MenuItem("Presensi", Icons.AutoMirrored.Filled.FactCheck),
                        MenuItem("Penelitian", Icons.Default.Science),
                        MenuItem("Perpustakaan", Icons.AutoMirrored.Filled.LibraryBooks),
                        MenuItem("SIAKAD", Icons.Default.School),
                        MenuItem("Prestasi", Icons.Default.EmojiEvents),
                        MenuItem("Berita", Icons.Default.Newspaper)
                    )
                } else {
                    listOf(
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
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    menuItems.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    MenuIcon(item, onClick = { onNavigate(item.title) })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun KelasMendatangItem(title: String, subtitle: String, room: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0B194C))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(room, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

data class MenuItem(val title: String, val icon: ImageVector)

@Composable
fun MenuIcon(item: MenuItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
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
