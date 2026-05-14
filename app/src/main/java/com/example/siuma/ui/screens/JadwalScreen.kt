package com.example.siuma.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val time: String,
    val isPresensiDone: Boolean = false
)

// --- ViewModel ---
class JadwalViewModel : ViewModel() {
    val daftarJadwal = mutableStateListOf(
        JadwalItem(1, "Pengembangan Aplikasi Bergerak", "12013220427 - Arif Rohmadi S.Kom., M.Cs", "B4.11", "Senin", "08:00 - 10:30", true),
        JadwalItem(2, "Kecerdasan Buatan", "12013220428 - Akhmad Syaifuddin S.Si., M.Cs.", "B4.04", "Selasa", "13:00 - 15:30", false),
        JadwalItem(3, "Basis Data", "12013220429 - Bambang Widoyono S.T., M.T.I.", "Lab Komputer 1", "Rabu", "10:00 - 12:30", true),
        JadwalItem(4, "Sistem Operasi", "12013220430 - Herdito Ibnu Dewangkoro M.Kom.", "B4.05", "Kamis", "07:30 - 10:00", true),
        JadwalItem(5, "Teori Bahasa & Automata", "12013220431 - HERI PRASETYO S.Kom, M.Sc.Eng.", "B4.11", "Jumat", "13:00 - 14:40", true)
    )

    fun markPresensiDone(id: Int) {
        val index = daftarJadwal.indexOfFirst { it.id == id }
        if (index != -1) {
            daftarJadwal[index] = daftarJadwal[index].copy(isPresensiDone = true)
        }
    }
}

@Composable
fun JadwalScreen(onBack: () -> Unit) {
    val viewModel: JadwalViewModel = viewModel()
    var selectedJadwal by remember { mutableStateOf<JadwalItem?>(null) }
    var presensiTarget by remember { mutableStateOf<JadwalItem?>(null) }

    BackHandler(enabled = selectedJadwal != null || presensiTarget != null) {
        if (presensiTarget != null) {
            presensiTarget = null
        } else {
            selectedJadwal = null
        }
    }
    
    if (presensiTarget != null) {
        PresensiConfirmationScreen(
            item = presensiTarget!!,
            onBack = { presensiTarget = null },
            onConfirm = {
                viewModel.markPresensiDone(presensiTarget!!.id)
                presensiTarget = null
                selectedJadwal = null // Kembali ke list setelah presensi
            }
        )
    } else if (selectedJadwal == null) {
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
                        if (!item.isPresensiDone) {
                            presensiTarget = item
                        } else {
                            selectedJadwal = item
                        }
                    }
                }
            }
        }
    } else {
        JadwalDetailScreen(
            item = selectedJadwal!!, 
            onBack = { selectedJadwal = null },
            onPresensiClick = { presensiTarget = selectedJadwal }
        )
    }
}

@Composable
fun JadwalCard(item: JadwalItem, onClick: () -> Unit) {
    val isUrgent = !item.isPresensiDone
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isUrgent) Color(0xFFFFF5F5) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(
            1.dp, 
            if (isUrgent) Color(0xFFB71C1C) else Color(0xFFE0E0E0)
        )
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
                    color = if (isUrgent) Color(0xFFB71C1C) else Color(0xFF0B194C)
                )
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0B194C),
                        modifier = Modifier.weight(1f)
                    )
                    if (isUrgent) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB71C1C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
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
                        tint = if (isUrgent) Color(0xFFB71C1C) else Color(0xFFFFC107)
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
fun JadwalDetailScreen(item: JadwalItem, onBack: () -> Unit, onPresensiClick: () -> Unit) {
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
            DetailInfoRow(label = "Status Presensi", value = if (item.isPresensiDone) "Hadir" else "Belum Dilakukan")
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { if (!item.isPresensiDone) onPresensiClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.isPresensiDone) Color(0xFF4CAF50) else Color(0xFF0B194C)
                )
            ) {
                if (item.isPresensiDone) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (item.isPresensiDone) "Presensi Selesai Dilakukan" else "Lakukan Presensi")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresensiConfirmationScreen(item: JadwalItem, onBack: () -> Unit, onConfirm: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val healthOptions = listOf("Sehat", "Sakit Demam/Batuk/Flu/Batuk")
    var selectedHealth by remember { mutableStateOf(healthOptions[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konfirmasi Presensi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF0B194C)
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B194C))
            ) {
                Text("Simpan dan Lakukan Presensi")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = item.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B194C)
            )
            Text(text = item.info, fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Langkah-langkah Presensi:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PresensiStep(number = "1", text = "Pastikan Anda berada di area kampus Universitas Sebelas Maret.")
                    PresensiStep(number = "2", text = "Hubungkan perangkat Anda ke jaringan WiFi resmi kampus (UNS-Hotspot).")
                    PresensiStep(number = "3", text = "Pilih kondisi kesehatan Anda saat ini pada menu pilihan di bawah.")
                    PresensiStep(number = "4", text = "Tekan tombol konfirmasi untuk menyimpan data kehadiran Anda.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Kondisi Kesehatan",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedHealth,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    healthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedHealth = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Data kehadiran akan tercatat secara otomatis.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PresensiStep(number: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$number.",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF0B194C),
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
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
