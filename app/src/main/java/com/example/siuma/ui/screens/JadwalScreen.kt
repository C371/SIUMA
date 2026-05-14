package com.example.siuma.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.InputStream

// Data Model
data class JadwalItem(
    val id: Int,
    val title: String,
    val info: String, // NIM/NIP - Dosen atau Kelas
    val room: String,
    val day: String,
    val time: String,
    val isPresensiDone: Boolean = false,
    val isDosenRole: Boolean = false
)

//ViewModel dipindahk ke AcademicViewModel di StudentViewModels.kt

@Composable
fun JadwalScreen(isDosen: Boolean = false, onBack: () -> Unit) {
    val academicViewModel: AcademicViewModel = viewModel()
    
    val schedules = if (isDosen) academicViewModel.daftarJadwalDosen else academicViewModel.daftarJadwalMahasiswa
    
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
        if (isDosen) {
            DosenPresensiScreen(
                item = presensiTarget!!,
                onBack = { presensiTarget = null },
                onConfirm = { materi ->
                    academicViewModel.markPresensiDone(presensiTarget!!.id, true, materi)
                    presensiTarget = null
                    selectedJadwal = null
                }
            )
        } else {
            PresensiConfirmationScreen(
                item = presensiTarget!!,
                onBack = { presensiTarget = null },
                onConfirm = {
                    academicViewModel.markPresensiDone(presensiTarget!!.id)
                    presensiTarget = null
                    selectedJadwal = null 
                }
            )
        }
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
                    text = if (isDosen) "Jadwal Mengajar" else "Jadwal Perkuliahan",
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
                items(schedules) { item ->
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

@Composable
fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFF5F5F5))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenPresensiScreen(item: JadwalItem, onBack: () -> Unit, onConfirm: (String) -> Unit) {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var materiText by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            capturedImage = BitmapFactory.decodeStream(inputStream)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presensi Mengajar", fontWeight = FontWeight.Bold) },
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
                onClick = { onConfirm(materiText) },
                enabled = capturedImage != null && materiText.isNotBlank(),
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

            Text(
                "Materi Perkuliahan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = materiText,
                onValueChange = { materiText = it },
                placeholder = { Text("Contoh: Pengenalan Kotlin & Compose") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Unggah Bukti Perkuliahan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                "Silakan unggah foto saat kegiatan belajar mengajar berlangsung sebagai bukti kehadiran.",
                fontSize = 13.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { photoLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (capturedImage != null) {
                    Image(
                        bitmap = capturedImage!!.asImageBitmap(),
                        contentDescription = "Bukti Foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tekan untuk Unggah Foto", color = Color.Gray)
                    }
                }
            }
            
            if (capturedImage != null) {
                TextButton(
                    onClick = { capturedImage = null },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Hapus dan Foto Ulang", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3F51B5))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Pastikan foto terlihat jelas dan mencakup suasana kelas atau materi yang diajarkan.",
                        fontSize = 12.sp,
                        color = Color(0xFF3F51B5)
                    )
                }
            }
        }
    }
}
