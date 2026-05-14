package com.example.siuma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// --- Data Model ---
data class JadwalItem(
    val id: Int,
    val title: String,
    val info: String, // NIM/NIP - Dosen
    val room: String,
    val day: String,
    val time: String
)

// --- ViewModel ---
class JadwalViewModel : ViewModel() {
    val daftarJadwal = listOf(
        JadwalItem(1, "Pengembangan Aplikasi Bergerak", "12013220427 - Arif Rohmadi S.Kom., M.Cs", "B4.11", "Senin", "08:00 - 10:30"),
        JadwalItem(2, "Kecerdasan Buatan", "12013220428 - Akhmad Syaifuddin S.Si., M.Cs.", "B4.04", "Selasa", "13:00 - 15:30"),
        JadwalItem(3, "Basis Data", "12013220429 - Bambang Widoyono S.T., M.T.I.", "Lab Komputer 1", "Rabu", "10:00 - 12:30"),
        JadwalItem(4, "Sistem Operasi", "12013220430 - Herdito Ibnu Dewangkoro M.Kom.", "B4.05", "Kamis", "07:30 - 10:00"),
        JadwalItem(5, "Teori Bahasa & Automata", "12013220431 - HERI PRASETYO S.Kom, M.Sc.Eng.", "B4.11", "Jumat", "13:00 - 14:40")
    )
}

@Composable
fun JadwalScreen(onBack: () -> Unit) {
    val viewModel: JadwalViewModel = viewModel()
    var selectedJadwal by remember { mutableStateOf<JadwalItem?>(null) }
    
    if (selectedJadwal == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Text(
                    text = "Jadwal Perkuliahan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B194C),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(viewModel.daftarJadwal) { item ->
                    JadwalCard(item) {
                        selectedJadwal = item
                    }
                }
            }
        }
    } else {
        JadwalDetailScreen(item = selectedJadwal!!, onBack = { selectedJadwal = null })
    }
}

@Composable
fun JadwalCard(item: JadwalItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day and Time Section
            Column(
                modifier = Modifier.width(85.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.day,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0B194C)
                )
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Vertical Divider
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // Course Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0B194C)
                )
                Text(
                    text = item.info,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.room,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun JadwalDetailScreen(item: JadwalItem, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "Detail Jadwal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B194C),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Surface(
                color = Color(0xFFE8EAF6),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Mata Kuliah",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF3F51B5),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B194C),
                lineHeight = 32.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DetailInfoRow(label = "Dosen Pengampu", value = item.info.substringAfter("- ").trim())
            DetailInfoRow(label = "NIP/NIDN", value = item.info.substringBefore(" -").trim())
            DetailInfoRow(label = "Hari", value = item.day)
            DetailInfoRow(label = "Waktu", value = item.time)
            DetailInfoRow(label = "Ruangan", value = item.room)
            DetailInfoRow(label = "SKS", value = "3 SKS") // Placeholder SKS
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { /* Bisa tambah fitur lain seperti lihat materi atau presensi */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B194C))
            ) {
                Text("Lihat Materi Kuliah")
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFF5F5F5))
    }
}
