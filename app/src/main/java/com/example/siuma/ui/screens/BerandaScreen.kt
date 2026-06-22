package com.example.siuma.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.School
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
import com.example.siuma.data.local.UserSession
import com.example.siuma.data.remote.dto.KelasDto
import com.example.siuma.ui.UiState

@Composable
fun BerandaScreen(
    session: UserSession?,
    onOpenKelas: () -> Unit,
    onOpenAkademik: () -> Unit,
    onOpenRekap: () -> Unit,
    onOpenKelasDetail: (Int) -> Unit,
    onOpenTugas: (Int) -> Unit,
    viewModel: BerandaViewModel = hiltViewModel()
) {
    val isDosen = session?.isDosen ?: false
    val context = LocalContext.current

    LaunchedEffect(isDosen) { viewModel.load(isDosen) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(SimuaBg),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Kartu sapaan (F-HOME-02)
        item { SapaanCard(session) }

        // Akses cepat (F-HOME-05)
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccess(Icons.Default.School, "Kelas", Modifier.weight(1f), onOpenKelas)
                if (isDosen) {
                    QuickAccess(Icons.AutoMirrored.Filled.FactCheck, "Rekap", Modifier.weight(1f), onOpenRekap)
                } else {
                    QuickAccess(Icons.AutoMirrored.Filled.Assignment, "Akademik", Modifier.weight(1f), onOpenAkademik)
                    QuickAccess(Icons.AutoMirrored.Filled.FactCheck, "Rekap", Modifier.weight(1f), onOpenRekap)
                }
            }
        }

        when (val state = viewModel.state) {
            is UiState.Loading -> item { Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { CircularProgressIndicator(color = SimuaNavy) } }
            is UiState.Error -> item { ErrorInline(state.message) { viewModel.load(isDosen) } }
            is UiState.Success -> {
                val data = state.data
                if (isDosen) {
                    item { SectionHeader("Kelas yang Diampu") }
                    if (data.kelas.isEmpty()) item { EmptyInline("Belum ada kelas.") }
                    else items(data.kelas) { k -> KelasRingkasCard(k, onClick = { onOpenKelasDetail(k.id) }) }
                } else {
                    item { SectionHeader("Tugas Mendatang") }
                    if (data.tugasMendatang.isEmpty()) item { EmptyInline("Tidak ada tugas yang perlu dikumpulkan. 🎉") }
                    else items(data.tugasMendatang) { t ->
                        TugasRingkasCard(t, onClick = { onOpenTugas(t.id) })
                    }

                    item { SectionHeader("Materi Terbaru") }
                    if (data.materiTerbaru.isEmpty()) item { EmptyInline("Belum ada materi.") }
                    else items(data.materiTerbaru) { m ->
                        MateriRingkasCard(m, onClick = { openUrl(context, m.fileUrl) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SapaanCard(session: UserSession?) {
    val nama = session?.name ?: "Pengguna"
    val isDosen = session?.isDosen ?: false
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SimuaNavy)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(SimuaGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(nama.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = SimuaNavy)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text("Halo,", color = Color(0xFFB0BEC5), fontSize = 13.sp)
            Text(nama, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            val sub = listOfNotNull(if (isDosen) "Dosen" else "Mahasiswa", session?.prodi).joinToString(" • ")
            Text(sub, color = SimuaGold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun QuickAccess(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = SimuaNavy)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, color = SimuaNavy, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = SimuaNavy
    )
}

@Composable
private fun TugasRingkasCard(t: TugasRingkas, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)), Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color(0xFFEF6C00), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(t.judul, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 14.sp)
                Text(t.kelasNama, fontSize = 12.sp, color = Color.Gray)
                t.deadline?.let { Text("Tenggat: ${formatTanggal(it)}", fontSize = 11.sp, color = Color(0xFFD32F2F)) }
            }
        }
    }
}

@Composable
private fun MateriRingkasCard(m: MateriRingkas, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)), Alignment.Center) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(m.judul, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 14.sp)
                Text(m.kelasNama, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun KelasRingkasCard(k: KelasDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp)), Alignment.Center) {
                Icon(Icons.Default.School, contentDescription = null, tint = SimuaNavy, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(k.nama, fontWeight = FontWeight.Bold, color = SimuaNavy, fontSize = 14.sp)
                Text("${k.kode} • ${k.sks} SKS", fontSize = 12.sp, color = Color.Gray)
            }
            Text("${k.jumlahTugas ?: 0} tugas", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun EmptyInline(text: String) {
    Text(text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color.Gray, fontSize = 13.sp)
}

@Composable
private fun ErrorInline(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = SimuaNavy)) { Text("Coba Lagi") }
    }
}
