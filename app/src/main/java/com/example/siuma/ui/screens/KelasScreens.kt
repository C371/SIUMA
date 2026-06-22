package com.example.siuma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.siuma.data.remote.dto.JadwalDto
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.data.remote.dto.MahasiswaDto
import com.example.siuma.data.remote.dto.MateriDto
import com.example.siuma.ui.UiState

// ============================ Daftar Kelas ============================

@Composable
fun KelasScreen(
    isDosen: Boolean,
    onBack: () -> Unit,
    onOpenKelas: (Int) -> Unit,
    viewModel: KelasViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadList() }

    BackScaffold(title = if (isDosen) "Kelas yang Diampu" else "Kelas yang Diambil", onBack = onBack) { padding ->
        when (val state = viewModel.listState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadList() }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyBox(if (isDosen) "Belum ada kelas yang diampu." else "Anda belum mengambil kelas.", Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { kelas ->
                            KelasCard(kelas, onClick = { onOpenKelas(kelas.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KelasCard(kelas: KelasDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(kelas.nama, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                "${kelas.kode} • ${kelas.sks} SKS" + (kelas.dosen?.let { " • $it" } ?: ""),
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                kelas.jumlahMateri?.let { CountChip(Icons.Default.Description, "$it materi") }
                kelas.jumlahTugas?.let { CountChip(Icons.AutoMirrored.Filled.Assignment, "$it tugas") }
            }
        }
    }
}

@Composable
private fun CountChip(icon: ImageVector, text: String) {
    Surface(color = Color(0xFFF0F4F8), shape = RoundedCornerShape(6.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = SimuaNavy)
            Spacer(Modifier.width(4.dp))
            Text(text, fontSize = 11.sp, color = SimuaNavy)
        }
    }
}

// ============================ Detail Kelas ============================

@Composable
fun KelasDetailScreen(
    kelasId: Int,
    isDosen: Boolean,
    onBack: () -> Unit,
    onOpenMahasiswa: (Int) -> Unit,
    onOpenTugas: (Int) -> Unit,
    onOpenKuis: (Int) -> Unit,
    onOpenPresensi: (Int) -> Unit,
    onUploadMateri: (Int) -> Unit,
    viewModel: KelasViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(kelasId) { viewModel.loadDetail(kelasId) }

    BackScaffold(title = "Detail Kelas", onBack = onBack) { padding ->
        when (val state = viewModel.detailState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadDetail(kelasId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                val kelas = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SimuaNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(kelas.nama, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("${kelas.kode} • ${kelas.sks} SKS", color = SimuaGold, fontSize = 13.sp)
                            kelas.dosen?.let {
                                Text("Pengampu: $it", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    // Jadwal
                    JadwalCard(kelas.jadwal)

                    // Navigasi fitur kelas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column {
                            FeatureRow(Icons.AutoMirrored.Filled.Assignment, "Tugas", onClick = { onOpenTugas(kelasId) })
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            FeatureRow(Icons.Default.Description, "Kuis", onClick = { onOpenKuis(kelasId) })
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            FeatureRow(Icons.Default.Schedule, "Presensi", onClick = { onOpenPresensi(kelasId) })
                            if (isDosen) {
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                                FeatureRow(Icons.Default.Groups, "Daftar Mahasiswa", onClick = { onOpenMahasiswa(kelasId) })
                            }
                        }
                    }

                    // Materi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Materi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SimuaNavy)
                        if (isDosen) {
                            TextButton(onClick = { onUploadMateri(kelasId) }) { Text("+ Unggah") }
                        }
                    }
                    if (kelas.materi.isEmpty()) {
                        Text("Belum ada materi.", color = Color.LightGray, fontSize = 13.sp)
                    } else {
                        kelas.materi.forEach { materi ->
                            MateriCard(materi, onClick = { openUrl(context, materi.fileUrl) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JadwalCard(jadwal: JadwalDto?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = SimuaNavy)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Jadwal", fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 14.sp)
                val hariJam = listOfNotNull(
                    jadwal?.hari,
                    if (jadwal?.jamMulai != null) "${jadwal.jamMulai}–${jadwal.jamSelesai ?: ""}" else null
                ).joinToString("  ")
                Text(hariJam.ifBlank { "Jadwal belum diatur" }, fontSize = 13.sp, color = Color.Gray)
                jadwal?.ruang?.let {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(it, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SimuaNavy)
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 15.sp, color = SimuaNavy, modifier = Modifier.weight(1f))
        Text("›", fontSize = 20.sp, color = Color.Gray)
    }
}

@Composable
fun MateriCard(materi: MateriDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFD32F2F))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(materi.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SimuaNavy)
                materi.keterangan?.takeIf { it.isNotBlank() }?.let {
                    Text(it, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                }
                Text(
                    if (materi.fileUrl != null) "Ketuk untuk membuka" else "Berkas tidak tersedia",
                    fontSize = 11.sp,
                    color = if (materi.fileUrl != null) SimuaNavy else Color.LightGray
                )
            }
        }
    }
}

// ============================ Daftar Mahasiswa ============================

@Composable
fun MahasiswaScreen(
    kelasId: Int,
    onBack: () -> Unit,
    viewModel: KelasViewModel = hiltViewModel()
) {
    LaunchedEffect(kelasId) { viewModel.loadMahasiswa(kelasId) }

    BackScaffold(title = "Daftar Mahasiswa", onBack = onBack) { padding ->
        when (val state = viewModel.mahasiswaState) {
            is UiState.Loading -> LoadingBox(Modifier.padding(padding))
            is UiState.Error -> ErrorBox(state.message, onRetry = { viewModel.loadMahasiswa(kelasId) }, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyBox("Belum ada mahasiswa terdaftar.", Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { mhs -> MahasiswaCard(mhs) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MahasiswaCard(mhs: MahasiswaDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8EAF6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(mhs.nama.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(mhs.nama, fontWeight = FontWeight.Bold, color = SimuaNavy)
                val detail = listOfNotNull(
                    mhs.nim?.let { "NIM: $it" },
                    mhs.prodi,
                    mhs.semester
                ).joinToString(" • ")
                if (detail.isNotBlank()) Text(detail, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
